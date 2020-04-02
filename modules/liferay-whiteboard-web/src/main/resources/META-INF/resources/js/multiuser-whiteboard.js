/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

AUI.add('multiuser-whiteboard', function (A, NAME) {

    var onlineUsersTemplateFn = null;
    var usersTooltipsTemplateFn = null;

    var CONTAINER = 'container';
    var COMMANDS = 'commands';
    var COMM = 'comm';
    var JOINING = 'joining';
    var EDITOR_ID = 'editorId';
    var SELECTOR_USERS_ONLINE = '.users-online';
    var SELECTOR_USER_MOD_TOOLTIPS = '.user-modification-tooltips';

    var MultiuserEditor = A.Base.create('multiuser-whiteboard', A.EditorManager, [], {

        supported: true, // flag set after verification is current browser is supported

        disconnectedModalMessage: null,

        keepAliveTimer: null, // timer that keeps websocket alive
        
        initializer: function () {
            var instance = this;

            onlineUsersTemplateFn = A.Handlebars.compile(this.get('onlineUsersTemplate'));
            usersTooltipsTemplateFn = A.Handlebars.compile(this.get('usersTooltipsTemplate'));

            this.bindCommEvents();
            this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .expand-collapse-btn').on('click', function (e) {
                e.preventDefault();
                instance.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .expand-collapse-btn').toggleClass('selected');
                instance.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .users-online-wrapper').toggleClass('show');
            });
        },

        bindCommEvents: function () {
            var instance = this;
            if (!window.WebSocket) { // if websocket not supported from current browser
                instance.supported = false;
                return;
            };
            var websocket = new WebSocket(instance.get('websocketAddress'));
            websocket.onopen = function (evt) {
                websocket.send(A.JSON.stringify({
                    type: MultiuserEditor.CONSTANTS.LOGIN
                }));
                instance.keepAlive(websocket);
            };
            websocket.onclose = function (evt) {
                instance.fire('connectionClosed');
                instance.cancelKeepAlive();
            };
            websocket.onmessage = function (event) {
                instance.processMessage(function (data) {
                    instance.executeCommands(data.commands || []);
                    /* if user is currently joining the whiteboard, load the whiteboard  dump to show shapes previously created */
                    if (instance.get(JOINING)) {
                        instance.executeCommands(data.dump);
                        instance.set(JOINING, false);
                    }
                }, A.JSON.parse(event.data));
            };
            websocket.onerror = function (evt) {
                console.error(evt);
            };
            /* broadcast */
            window.setInterval(function () {
                if (!instance.get(COMMANDS).length) {
                    return;
                }
                websocket.send(instance.stringifyCommands()); /* stringify not supported on old browsers */
                instance.set(COMMANDS, []);
            }, MultiuserEditor.CONSTANTS.BROADCAST_INTERVAL);
            // when SPA (single page app) navigation is configured
            // need to listen beforeNavigate event to manually close connection
            Liferay.on('beforeNavigate', function (event) {
                websocket.close();
            });
        },

        keepAlive: function (websocket) {
            var instance = this;
            var timeout = 20000;  
            if (websocket.readyState == websocket.OPEN) {  
                websocket.send(A.JSON.stringify({keepAlive: true}));  
            }  
            this.keepAliveTimer = window.setTimeout(function() {
                instance.keepAlive(websocket);
            }, timeout);  
        },
        
        cancelKeepAlive: function() {  
            if (this.keepAliveTimer) {  
                window.clearTimeout(this.keepAliveTimer);  
            }  
        },

        /**
         * Checks if message is not coming from the same client, if no, does not continue
         * 
         * 
         */
        processMessage: function (callback, data) {
            if (data.editorId != this.get(EDITOR_ID)) {
                callback(data);
            }
        },


        /**
         * Transforms commands and adds editor id to a json string
         * 
         * 
         */
        stringifyCommands: function () {
            var commands = this.addUserTooltipCommand(this.get(COMMANDS));
            return A.JSON.stringify({
                editorId: this.get(EDITOR_ID),
                commands: commands
            });
        },

        /**
         * Add the last user interaction coords and username to the commands to be sent to the other users to
         * know which shapes are being modified and who is
         * 
         * 
         */
        addUserTooltipCommand: function (commands) {
            if (commands.length > 0) {
                var lastCommand = commands[commands.length - 1];
                var state = lastCommand.state.top ? lastCommand.state : lastCommand.state.options;
                if (state && state.top && state.left) {
                    commands.push({
                        action: MultiuserEditor.CONSTANTS.TOOLTIP,
                        id: this.get(EDITOR_ID),
                        userName: this.get('userName'),
                        userImagePath: this.get('userImagePath'),
                        top: state.top - 55, // decrease top to avoid overlapping with the shape
                        left: state.left
                    });
                }
            }
            return commands;
        },

        /**
         * Executes a list of commands at the same time
         * 
         */
        executeCommands: function (commands) {
            for (var i = 0; i < commands.length; i++) {
                var command = commands[i];
                command.remotelyTriggered = true;
                if (command.action == A.EditorManager.CONSTANTS.CREATE) {
                    this.createShape(command);
                }
                /* if is no new shape, look the current shape reference from cache */
                var cachedShape = this.getShapeFromCache(command.cacheId);

                if (command.action == A.EditorManager.CONSTANTS.MODIFY && cachedShape) {
                    this.discardActiveObjects();
                    this.modifyShape(command);
                }
                if (command.action == A.EditorManager.CONSTANTS.DELETE && cachedShape) {
                    this.deleteShapeFromCache(command.cacheId);
                    cachedShape.remove();
                }
                if (command.action == A.EditorManager.CONSTANTS.SENT_TO_BACK && cachedShape) {
                    cachedShape.sendToBack();
                }
                if (command.action == A.EditorManager.CONSTANTS.BRING_TO_FRONT && cachedShape) {
                    cachedShape.bringToFront();
                }
                if (command.action == MultiuserEditor.CONSTANTS.USERS) {
                    this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .count').set('text', command.users.length);
                    this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .bd').empty();
                    this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .bd').append(onlineUsersTemplateFn(command));
                    this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS).empty();
                }
                if (command.action == MultiuserEditor.CONSTANTS.TOOLTIP) {
                    var userTooltipNode = this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS + ' #' + command.id);
                    if (userTooltipNode) {
                        userTooltipNode.setStyles({
                            top: command.top + 'px',
                            left: command.left + 'px'
                        });
                    } else {
                        this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS).append(usersTooltipsTemplateFn(command));
                        userTooltipNode = this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS + ' #' + command.id);
                    }
                    this.animateTooltip(userTooltipNode);
                }
            }
        },

        discardActiveObjects() {
            this.get('canvas').discardActiveObject();
            return this.get('canvas');
        },

        /**
         * Animates user tooltip when user is modifying something in the canvas, animation will be shown to other users looking the same shape
         * that is being modified
         * 
         */
        animateTooltip: function (node) {
            var animStart = new A.Anim({
                node: node,
                to: {
                    opacity: 1
                }
            });
            var animEnd = new A.Anim({
                node: node,
                to: {
                    opacity: 0
                }
            });
            animStart.on('end', function () {
                animEnd.run();
            });

            animStart.run();
        }

    }, {
        ATTRS: {

            /**
             * Websocket address
             * 
             */
            websocketAddress: {
                value: ''
            },

            /**
             * Online users list html  template
             * 
             */
            onlineUsersTemplate: {
                value: ''
            },

            /**
             * Users tooltips to display which shapes the users are modifying
             * 
             */
            usersTooltipsTemplate: {
                value: ''
            },

            /**
             * When user is joining to the whiteboard communication
             * 
             */
            joining: {
                value: true
            },

            /**
             * Profile image path
             * 
             */
            baseImagePath: {
                value: ''
            },

            /**
             * Username and user image to be displayed to the other users in tooltips while editing the whiteboard
             * 
             */
            userName: {
                value: ''
            },
            userImagePath: {
                value: ''
            }

        }
    });

    MultiuserEditor.CONSTANTS = {
        BROADCAST_INTERVAL: 10, // millisecs interval used to send updates to the rest of connected editor clients, made as it to avoid performance issues,
        LOGIN: 'login',
        USERS: 'users',
        TOOLTIP: 'tooltip'
    };


    A.MultiuserEditor = MultiuserEditor;

}, '@VERSION@', {
    "requires": ["yui-base", "base-build", "whiteboard", "json-parse", "json-stringify", "handlebars", "aui-modal", "anim"]
});
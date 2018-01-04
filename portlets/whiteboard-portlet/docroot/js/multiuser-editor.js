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

YUI.add('multiuser-whiteboard', function (Y, NAME) {

    var onlineUsersTemplateFn = null;
    var usersTooltipsTemplateFn = null;

    var CONTAINER = 'container';
    var COMMANDS = 'commands';
    var COMM = 'comm';
    var JOINING = 'joining';
    var EDITOR_ID = 'editorId';
    var SELECTOR_USERS_ONLINE = '.users-online';
    var SELECTOR_USER_MOD_TOOLTIPS = '.user-modification-tooltips';

    var MultiuserEditor = Y.Base.create('multiuser-whiteboard', Y.EditorManager, [], {

        disconnectedModalMessage: null,

        initializer: function () {
            var instance = this;

            var canvasWidth = instance.get('containerWidth');
            if (canvasWidth == 0) {
            		canvasWidth = Math.round((70 * window.innerWidth) / 100);
            }

            if (!!instance.get('canvas') && instance.get('canvas').width == 0) {
              	instance.get('canvas').setWidth(canvasWidth);
              	instance.get('canvas').setHeight(500);
            }

            onlineUsersTemplateFn = Y.Handlebars.compile(this.get('onlineUsersTemplate'));
            usersTooltipsTemplateFn = Y.Handlebars.compile(this.get('usersTooltipsTemplate'));

            this.bindCommEvents();

            this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .expand-collapse-btn').on('click', function(e) {
                instance.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .expand-collapse-btn').toggleClass('selected');
                instance.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .users-online-wrapper').toggleClass('show');
            });

            this.disconnectedModalMessage = new Y.Modal({
                bodyContent: Liferay.Language.get('rivetlogic.whiteboard.connection.issues.message'),
                centered: true,
                headerContent: Liferay.Language.get('rivetlogic.whiteboard.message.title'),
                modal: true,
                visible: false,
                width: 450,
                zIndex: Liferay.zIndex.TOOLTIP
            }).render();

            instance.get('canvas').renderAll();
            instance.get('canvas').calcOffset();
        },

        bindCommEvents: function () {
            if (this.get('useAtmosphere')) {
                this.bindAtmosphere();
            }
        },

        /**
         * Use atmosphere framework for real time communication
         *
         *
         */
        bindAtmosphere: function() {
            var instance = this;
            var baseUrl = document.location.toString().split('/').slice(0, 3).join('/'); // gets only protocol, domain and port from current url
            var request = {
                url: baseUrl + '/delegate/collaboration-whiteboard' + document.location.pathname + '?baseImagePath=' +
                    encodeURIComponent(instance.get('baseImagePath')),
                trackMessageLength: true,
                transport: 'websocket'
                //logLevel: 'debug',
            };

            request.onMessage = function (response) {
                instance.processMessage(function(data) {
                    instance.executeCommands(data.commands);
                    /* if user is currently joining the whiteboard, load the whiteboard  dump to show shapes previously created */
                    if (instance.get(JOINING)) {
                        instance.executeCommands(data.dump);
                        instance.set(JOINING, false);
                    }
                }, Y.JSON.parse(response.responseBody));
            };
            request.onOpen = function (response) {
                instance.get(COMM).push(Y.JSON.stringify({
                    type:  MultiuserEditor.CONSTANTS.LOGIN
                }));
            };

            request.onClose = function (response) {
                //instance.disconnectedModalMessage.show();
            };

            instance.set(COMM, atmosphere.subscribe(request));
            /* broadcast */
            window.setInterval(function () {
                if (!instance.get(COMMANDS).length) {
                    return;
                }
                instance.get(COMM).push(instance.stringifyCommands()); /* stringify not supported on old browsers */
                instance.set(COMMANDS, []);
            }, MultiuserEditor.CONSTANTS.BROADCAST_INTERVAL);
        },

        /**
         * Checks if message is not coming from the same client, if no, does not continue
         *
         *
         */
        processMessage: function(callback, data) {
            if (data.editorId != this.get(EDITOR_ID)) {
                callback(data);
            }
        },


        /**
         * Transforms commands and adds editor id to a json string
         *
         *
         */
        stringifyCommands: function() {
            var commands = this.addUserTooltipCommand(this.get(COMMANDS));
            return Y.JSON.stringify({editorId: this.get(EDITOR_ID), commands: commands});
        },

        /**
         * Add the last user interaction coords and username to the commands to be sent to the other users to
         * know which shapes are being modified and who is
         *
         *
         */
        addUserTooltipCommand: function(commands) {
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
                if (command.action == Y.EditorManager.CONSTANTS.CREATE) {
                    this.createShape(command);
                }
                /* if is no new shape, look the current shape reference from cache */
                var cachedShape = this.getShapeFromCache(command.cacheId);

                if (command.action == Y.EditorManager.CONSTANTS.MODIFY && cachedShape) {
                    this.get('canvas').discardActiveGroup();
                    this.modifyShape(command);
                }
                if (command.action == Y.EditorManager.CONSTANTS.DELETE && cachedShape) {
                    this.deleteShapeFromCache(command.cacheId);
                    cachedShape.remove();
                }
                if (command.action == MultiuserEditor.CONSTANTS.USERS) {
                    this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .count').set('text', command.users.length);
                    this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .bd').empty();
                    this.get(CONTAINER).one(SELECTOR_USERS_ONLINE + ' .bd').append(onlineUsersTemplateFn(command));
                    this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS).empty();
                }
                if (command.action == MultiuserEditor.CONSTANTS.TOOLTIP) {
                    var userTooltipNode = this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS+' #' + command.id);
                    if (userTooltipNode) {
                        userTooltipNode.setStyles({top: command.top + 'px', left: command.left + 'px'});
                    } else {
                        this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS).append(usersTooltipsTemplateFn(command));
                        userTooltipNode = this.get(CONTAINER).one(SELECTOR_USER_MOD_TOOLTIPS+' #' + command.id);
                    }
                    this.animateTooltip(userTooltipNode);
                }
            }
        },

        /**
         * Animates user tooltip when user is modifying something in the canvas, animation will be shown to other users looking the same shape
         * that is being modified
         *
         */
        animateTooltip: function(node) {
            var animStart = new Y.Anim({
                node: node,
                to: { opacity: 1 }
            });
            var animEnd = new Y.Anim({
                node: node,
                to: { opacity: 0 }
            });
            animStart.on('end', function() {
                animEnd.run();
            });

            animStart.run();
        }

    }, {
        ATTRS: {

            /**
             * Stores websocket object or atmosphere handler
             *
             */
            comm: {
                value: null
            },

            /**
             * Use atmosphere framework or not
             *
             */
            useAtmosphere: {
                value: false
            },

            /**
             * Websocket address provided if atmosphere is not going to be used
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
            },

            containerWidth: {
                value: 0
            }
        }
    });

    MultiuserEditor.CONSTANTS = {
        BROADCAST_INTERVAL: 10, // millisecs interval used to send updates to the rest of connected editor clients, made as it to avoid performance issues,
        LOGIN: 'login',
        USERS: 'users',
        TOOLTIP: 'tooltip'
    };

    Y.MultiuserEditor = MultiuserEditor;

}, '@VERSION@', {
    "requires": ["yui-base", "base-build", "whiteboard", "json-parse", "json-stringify", "handlebars", "aui-modal", "anim"]
});

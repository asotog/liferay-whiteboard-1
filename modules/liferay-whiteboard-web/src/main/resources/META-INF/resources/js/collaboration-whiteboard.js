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
AUI.add('collaboration-whiteboard-portlet', function (A, NAME) {

    A.CollaborationWhiteboardPortlet = {};

    /**
     * Inits whiteboard portlet
     * @param url {String} Websocket endpoint url
     */
    A.CollaborationWhiteboardPortlet.init = function (url, userFullName) {

        var SELECTOR_WHITEBOARD_PORTLET = '.whiteboard-portlet';
        var SELECTOR_CANVAS = 'canvas';
        var SELECTOR_EDITOR = '.editor';
        var SELECTOR_CONNECTION_LOST_MESSAGE = '.connection-lost-alert';
        var SELECTOR_CONNECTION_UNSUPPORTED_MESSAGE = '.communication-unsupported-alert';
        var SELECTOR_TEXT_EDITOR = '.text-editor';
        var SELECTOR_ONLINE_USERS_TEMPLATE = '#users-online-template';
        var SELECTOR_USER_TOOLTIPS_TEMPLATE = '#user-tooltips-template';
        var CANVAS_NAME = 'editor-canvas';
        var SPACE = ' ';
        var DASH = '-';

        var containerWidth = A.one(SELECTOR_WHITEBOARD_PORTLET + SPACE + SELECTOR_EDITOR).get('offsetWidth');
        A.one(SELECTOR_WHITEBOARD_PORTLET + SPACE + SELECTOR_CANVAS).setAttribute('width', containerWidth);

        var canvas = new fabric.Canvas(CANVAS_NAME);
        A.on(['orientationchange', 'resize'], function (e) {
        	var containerElement = A.one(SELECTOR_WHITEBOARD_PORTLET + SPACE + SELECTOR_EDITOR);
        	if (!containerElement) {
        		return;
        	}
            containerWidth = A.one(SELECTOR_WHITEBOARD_PORTLET + SPACE + SELECTOR_EDITOR).get('offsetWidth');
            canvas.setDimensions({
                width: containerWidth,
                height: 600
            });
        });

        var editor = new A.MultiuserEditor({
            canvas: canvas,
            websocketAddress: url,
            container: A.one(SELECTOR_WHITEBOARD_PORTLET + SPACE + SELECTOR_EDITOR),
            textEditorNode: A.one(SELECTOR_WHITEBOARD_PORTLET + SPACE + SELECTOR_TEXT_EDITOR),
            editorId: (Liferay.ThemeDisplay.getUserId() + DASH + Math.floor((Math.random() * 10) + 100)),
            onlineUsersTemplate: A.one(SELECTOR_ONLINE_USERS_TEMPLATE).get('innerHTML'),
            usersTooltipsTemplate: A.one(SELECTOR_USER_TOOLTIPS_TEMPLATE).get('innerHTML'),
            baseImagePath: Liferay.ThemeDisplay.getPathImage(),
            userName: (userFullName != '') ? userFullName : 'Guest',
            /* for user tooltip */
            userImagePath: A.one('.whiteboard-portlet .profile-image-path').get('value') /* for user tooltip */
        });

        editor.on('connectionClosed', function () {
            A.one(SELECTOR_WHITEBOARD_PORTLET).one(SELECTOR_CONNECTION_LOST_MESSAGE).removeClass(Y.CollaborationWhiteboardConstants.hiddenCSSClass);
        });
        // if multi user communication not supported by current browser we let know the user about the issue
        if (!editor.supported) {
            A.one(SELECTOR_WHITEBOARD_PORTLET).one(SELECTOR_CONNECTION_UNSUPPORTED_MESSAGE).removeClass(Y.CollaborationWhiteboardConstants.hiddenCSSClass);
        };

    }

}, '', {
    "requires": ['multiuser-whiteboard', 'fabricjs', 'collaboration-whiteboard-common']
});
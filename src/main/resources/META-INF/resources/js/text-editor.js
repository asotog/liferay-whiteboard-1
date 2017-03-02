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

YUI.add('text-editor', function (Y, NAME) {

    var EVT_TEXT_EDITED = 'text-editor:textedited';
    var CURRENT_TEXT_COMPONENT = 'currentTextComponent';
    var TEXT_EDITOR_NODE = 'textEditorNode';
    var TEXT_EDITOR = 'textEditor';
    var SELECTOR_CANCEL_BUTTON = '.cancel';
    var SELECTOR_EDIT_BUTTON = '.edit';
    var SELECTOR_TEXT = '.text';
    var TEXT_EDITOR_HEADER_LABEL = 'rivetlogic.whiteboard.edit.text';

    var strings = window.CollaborationWhiteboardPortlet.strings;

    var TextEditor = Y.Base.create('text-editor', Y.Base, [], {

        initializer: function () {
            var panel = new Y.Modal({
                headerContent: '',
                bodyContent: '',
                width: 250,
                zIndex: Liferay.zIndex.TOOLTIP,
                centered: true,
                visible: false,
                modal: true
            }).render();
            panel.set('headerContent', strings[TEXT_EDITOR_HEADER_LABEL]);
            panel.get('boundingBox').one('.modal-body').append(this.get(TEXT_EDITOR_NODE));
            this.get(TEXT_EDITOR_NODE).removeClass('hidden');
            this.set(TEXT_EDITOR, panel);
            this.bindTextEditor();
        },

        bindTextEditor: function () {
            var instance = this;
            this.get(TEXT_EDITOR_NODE).one(SELECTOR_CANCEL_BUTTON).on('click', function () {
                instance.get(TEXT_EDITOR).hide();
            });
            this.get(TEXT_EDITOR_NODE).one(SELECTOR_EDIT_BUTTON).on('click', function () {
                instance.get(CURRENT_TEXT_COMPONENT).setText(instance.get(TEXT_EDITOR_NODE).one(SELECTOR_TEXT).get('value'));
                instance.get(CURRENT_TEXT_COMPONENT).fire('modified');
                instance.fire(EVT_TEXT_EDITED);
                instance.get(TEXT_EDITOR).hide();
            });
        },

        /**
         * Shows the text editor popup, called outside of this class
         * 
         */
        editText: function (textComponent) {
            this.set(CURRENT_TEXT_COMPONENT, textComponent);
            this.get(TEXT_EDITOR).show();
            this.get(TEXT_EDITOR_NODE).one(SELECTOR_TEXT).set('value', textComponent.text);
        }
    }, {
        ATTRS: {
            textEditorNode: null,
            textEditor: null,
            currentTextComponent: null
        }
    });

    Y.TextEditor = TextEditor;

}, '@VERSION@', {
    "requires": ["yui-base", "base-build", "panel", "dd-plugin", "aui-modal"]
});
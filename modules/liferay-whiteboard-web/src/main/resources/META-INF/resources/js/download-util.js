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

YUI.add('download-util', function (Y, NAME) {
	
	var strings = window.CollaborationWhiteboardPortlet.strings;
	
    var EditorDownload = {

        /**
         * Shows modal to see how the output image is 
         * going to look and be able to download it
         *
         */
        show: function (canvas) {
            var modal = new Y.Modal({
                bodyContent: ' ',
                centered: true,
                destroyOnHide: true,
                headerContent: strings['rivetlogic.whiteboard.download.downloadaction'],
                modal: true,
                render: 'body',
                resizable: {
                    handles: 'b, r'
                },
                visible: true,
                zIndex: 9999
            }).render();

            canvas.backgroundColor = 'white';

            var imageData = canvas.toDataURL({
                format: 'jpeg',
                multiplier: 1
            });
            EditorDownload._addImg(imageData, modal);

            modal.align();

            modal.addToolbar([{
                label: Liferay.Language.get('cancel'),
                on: {
                    click: function () {
                        modal.hide();
                    }
                }
            }, {
                label: strings['rivetlogic.whiteboard.download.downloadaction'],
                icon: 'icon-download-alt',
                on: {
                    click: function () {
                        var anchor = Y.Node.create('<a/>');
                        anchor.setAttribute('target', '_blank');
                        anchor.setAttribute('download', 'whiteboard-image.jpeg');
                        anchor.setAttribute('href', imageData)
                        anchor.simulate('click');
                    }
                }
            }]);

        },

        /**
         * shows preview image in the popup
         *
         *
         */
        _addImg: function (data, modal) {
            var img = Y.Node.create('<img/>');
            img.setAttribute('src', data);
            modal.get('contentBox').one('.modal-body').addClass('whiteboard-download-modal');
            modal.get('contentBox').one('.modal-body').append(img);
        }
    }

    Y.EditorDownload = EditorDownload;

}, '@VERSION@', {
    "requires": ["aui-download", "yui-base", "base-build", "node-event-simulate"]
});
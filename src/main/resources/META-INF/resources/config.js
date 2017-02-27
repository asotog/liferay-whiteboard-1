;(function() {
	var timestamp = new Date().getTime();
	AUI().applyConfig({
		groups : {
			whiteboard : {
				base : MODULE_PATH + '/js/',
				combine : Liferay.AUI.getCombine(),
				modules : {
					'collaboration-whiteboard-portlet' : {
						path : 'collaboration-whiteboard.js?t=' + timestamp,
						requires : []
					},
					'multiuser-whiteboard' : {
						path : 'multiuser-whiteboard.js?t=' + timestamp,
						requires : []
					},
					'whiteboard' : {
						path : 'whiteboard.js?t=' + timestamp,
						requires : []
					},
					'text-editor' : {
						path : 'text-editor.js?t=' + timestamp,
						requires : []
					},
					'color-picker' : {
						path : 'color-picker.js?t=' + timestamp,
						requires : []
					},
					'download-util' : {
						path : 'download-util.js?t=' + timestamp,
						requires : []
					},
					'fabricjs' : {
						path : 'third-party/fabric.min.js?t=' + timestamp,
						requires : []
					}
				},
				root : MODULE_PATH + '/js/'
			}
		}
	});
})();
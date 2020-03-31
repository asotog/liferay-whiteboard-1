;(function() {
	AUI().applyConfig({
		groups : {
			whiteboard : {
				base : MODULE_PATH + '/js/',
				combine : Liferay.AUI.getCombine(),
				modules : {
					'collaboration-whiteboard-portlet' : {
						path : 'collaboration-whiteboard.js',
						requires : []
					},
					'collaboration-whiteboard-common' : {
						path : 'common.js',
						requires : []
					},
					'multiuser-whiteboard' : {
						path : 'multiuser-whiteboard.js',
						requires : []
					},
					'whiteboard' : {
						path : 'whiteboard.js',
						requires : []
					},
					'text-editor' : {
						path : 'text-editor.js',
						requires : []
					},
					'color-picker' : {
						path : 'color-picker.js',
						requires : []
					},
					'download-util' : {
						path : 'download-util.js',
						requires : []
					},
					'fabricjs' : {
						path : 'third-party/fabric.min.js',
						requires : []
					}
				},
				root : MODULE_PATH + '/js/'
			}
		}
	});
})();
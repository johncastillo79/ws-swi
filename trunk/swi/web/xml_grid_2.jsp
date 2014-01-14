
<%-- 
    Document   : John
    Created on : 26-04-2011, 02:30:51 PM
    Author     : John
--%>

<html>
    <head>
        <title>XML GRID</title>
        <!-- ExtJS UI Framework -->
        <link rel="stylesheet" type="text/css" href="/ext-3.3.1/resources/css/ext-all.css" />
        <script type="text/javascript" src="/ext-3.3.1/adapter/ext/ext-base.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/ext-all.js"></script>    
        <link rel="stylesheet" type="text/css" href="css/icons.css"/>

        <!--        <link rel="stylesheet" type="text/css" href="/ext-3.3.1/examples/ux/fileuploadfield/css/fileuploadfield.css" />-->
        <!--        <script type="text/javascript" src="/ext-3.3.1/examples/ux/fileuploadfield/FileUploadField.js"></script>-->
        <script type="text/javascript" src="http://dev.sencha.com/deploy/ext-3.4.0/examples/ux/fileuploadfield/FileUploadField.js"></script>

        <script type="text/javascript">
            Ext.BLANK_IMAGE_URL = '/ext-3.3.1/resources/images/default/s.gif';
            Ext.ns('com.icg');

            /**
             * 
             * @param {type} json
             * @returns {@exp;Ext@pro;util@pro;JSON@call;decode}
             */
            function jsonArbolNodoToGrid(json) {
                var data = Ext.util.JSON.decode(json);
                var str = '[{';
                function setter(node) {
                    if (node.parent) {
                        for (var prop in node.attributes) {
                            str = str + '"' + node.name + '/' + prop + '":"' + node.attributes[prop] + '",';
                        }
                        setter(node.parent);
                    }
                }

                function decoderTree(node) {

                    if (node.children) {
                        Ext.each(node.children, function(chld) {
                            chld.parent = node;
                            decoderTree(chld);
                        });
                    } else {
                        setter(node);
                        str = str.substring(0, str.length - 1) + '},{'
                    }
                }

                data.parent = null;
                decoderTree(data);
                str = str.substring(0, str.length - 2) + ']';
                return Ext.util.JSON.decode(str);
            }

            com.icg.FileUpload = {
                init: function() {
                    Ext.QuickTips.init();

                    var form = new Ext.FormPanel({
                        url: 'parsexml',
                        border: false,
                        bodyStyle: 'padding:10px',
                        labelAlign: 'top',
                        frame: false,
                        items: [{
                                xtype: 'textfield',
                                fieldLabel: 'XPath',
                                anchor: '100%',
                                name: 'xpath'
                            }, {
                                xtype: 'textarea',
                                anchor: '100% -10',
                                fieldLabel: 'XML File',
                                name: 'xml'
                            }]
                    });

                    var centro = new Ext.Panel({
                        layout: 'fit',
                        title: 'Grid Result',
                        region: 'center',
                        items: []
                    });

                    new Ext.Viewport({
                        layout: 'border',
                        border: false,
                        items: [{
                                title: 'XML to GRID Parser V1.0',
                                region: 'west',
                                split: true,
                                layout: 'fit',
                                width: 450,
                                minWidth: 400,
                                buttonAlign: 'center',
                                items: form,
                                buttons: [{
                                        text: 'Enviar',
                                        handler: function() {
                                            Ext.Ajax.request({
                                                url: 'parsexml',
                                                method: 'POST',
                                                params: form.getForm().getValues(),
                                                success: function(result, request) {
  
                                                    var sm = null;
                                                    var gridcfg = null;

                                                    var gdata = jsonArbolNodoToGrid(result.responseText);
                                                    var cols = new Array();
                                                    if (sm) {
                                                        cols.push(sm);
                                                    } else if (gdata.length > 5) {
                                                        cols.push(new Ext.grid.RowNumberer({
                                                            width: 29
                                                        }));
                                                    }
                                                    var isObject = function(val) {
                                                        if (val instanceof Object) {
                                                            console.log(val);
                                                            return 'Mas..'
                                                        } else {
                                                            return val;
                                                        }
                                                    };

                                                    var fields = new Array();
                                                    for (var prop in gdata[0]) {
                                                        if (prop !== '_root_') {
                                                            if (gridcfg) {
                                                                if (gridcfg[prop] !== '') {
                                                                    cols.push({
                                                                        header: gridcfg[prop],
                                                                        dataIndex: prop,
                                                                        sortable: true,
                                                                        renderer: isObject
                                                                    });
                                                                }
                                                            } else {
                                                                cols.push({
                                                                    header: prop,
                                                                    dataIndex: prop,
                                                                    sortable: true,
                                                                    renderer: isObject
                                                                });
                                                            }
                                                        }
                                                        var field = {
                                                            name: prop
                                                        };
                                                        fields.push(field)
                                                    }

                                                    var gridcfg = {
                                                        store: new Ext.data.JsonStore({
                                                            fields: fields,
                                                            data: gdata,
                                                            autoLoad: true
                                                        }),
                                                        columns: cols
                                                    };
                                                    if (sm) {
                                                        gridcfg.sm = sm;
                                                    }
                                                    var grid = new Ext.grid.GridPanel(gridcfg);
                                                    centro.removeAll();
                                                    centro.add(grid);
                                                    centro.doLayout();
                                                },
                                                failure: function(result, request) {

                                                }
                                            });
                                        }
                                    }, {
                                        text: 'Cancelar',
                                        handler: function() {
                                            form.getForm().reset();
                                        }
                                    }]
                            }, centro]
                    });
                }
            }
            Ext.onReady(com.icg.FileUpload.init, com.icg.FileUpload);
        </script>
    </head>
    <body></body>
</html>


/*!
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 * @Author: John Castillo Valencia
 * john.gnu@gmail.com
 */
Ext.ns('Ext.samples');

(function() {

    Ext.samples.processor = function(json) {
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
        if (!data.leaf) {
            decoderTree(data);
            str = str.substring(0, str.length - 2) + ']';
            return Ext.util.JSON.decode(str);
        } else {
            var arr = new Array();
            if (data.attributes) {
                arr.push(data.attributes);
            }
            return arr;
        }
    };


    Ext.samples.Fields = function(data, gridcfg) {
        if (data.length > 0) {

            var fields = new Array();
            for (var prop in data[0]) {
                fields.push({
                    name: prop
                })
            }

            var cols = new Array();
            //Numbered
            if (sm) {
                cols.push(sm);
            } else if (data.length > 5) {
                cols.push(new Ext.grid.RowNumberer({
                    width: 29
                }));
            }
            if (!gridcfg) {
                for (var prop in data[0]) {
                    cols.push({
                        header: prop,
                        dataIndex: prop,
                        sortable: true
                    });
                }
            } else {
                //Omiting hidden field, For Users
                Ext.each(gridcfg, function(col) {
                    if (!col.hidden) {
                        cols.push(col);
                    }
                });
            }

            var grid = new Ext.grid.GridPanel({
                title: 'Resultados',
                store: new Ext.data.JsonStore({
                    fields: fields,
                    data: data,
                    autoLoad: true
                }),
                columns: cols
            });
            return grid;
        }
        return null;
    };

    Ext.samples.RequestForm = function(options) {
        var formreq = Ext.getCmp('form_request');
        formreq.removeAll();
        Ext.Ajax.request({
            url: Ext.SROOT + 'paneldeservicios/formserviceitems',
            method: 'POST',
            params: {
                id: options.id
            },
            success: function(result, request) {
                var sfields = Ext.util.JSON.decode(result.responseText);
                sfields.push({
                    xtype: 'hidden',
                    name: '_swi_userservice_id_',
                    value: options.id
                });

                var ppanel = new Ext.Panel({
                    xtype: 'panel',
                    region: 'center',
                    border: false,
                    layout: 'fit',
                    height: 200
                });

                var form = new Ext.FormPanel({
                    url: Ext.SROOT + 'webservicesystem',
                    border: false,
                    autoHeight: true,
                    region: 'north',
                    bodyStyle: 'padding:10px',
                    labelWidth: 130,
                    frame: true,
                    labelAlign: 'top',
                    defaults: {
                        msgTarget: 'side'
                    },
                    items: sfields,
                    tbar: [{
                            text: 'Ejecutar',
                            iconCls: 'play',
                            tooltip: 'Llamar la operaci&oacute;n del servicio',
                            handler: function() {
                                formreq.getEl().mask("Procesando...", "x-mask-loading");
                                form.getForm().submit({
                                    success: function(form, action) {
                                        formreq.getEl().unmask();
                                        var ro = Ext.util.JSON.decode(action.response.responseText);
                                        if (ro.gridcfg) {
                                            ro.gridcfg = Ext.util.JSON.decode(ro.gridcfg);
                                        }
                                        //console.log(ro.gridcfg);
                                        if (ro.result.length !== 0) {
                                            ppanel.removeAll();
                                            var grid = Ext.samples.Fields(Ext.samples.processor(ro.result), ro.gridcfg);
                                            ppanel.add(grid);
                                            ppanel.doLayout();
                                        } else {
                                            Ext.MessageBox.show({
                                                title: 'Error',
                                                msg: 'Error del servidor, no hay datos',
                                                buttons: Ext.MessageBox.OK,
                                                icon: Ext.Msg.ERROR
                                            });
                                        }
                                    },
                                    failure: function(form, action) {
                                        formreq.getEl().unmask();
                                        Ext.MessageBox.show({
                                            title: 'Error',
                                            msg: 'Error del servidor',
                                            buttons: Ext.MessageBox.OK,
                                            icon: Ext.Msg.ERROR
                                        });
                                    }
                                });
                            }
                        }]
                });

                var panel = new Ext.Panel({
                    layout: 'border',
                    items: [form, ppanel]
                });

                formreq.add(panel);
                formreq.doLayout();
            },
            failure: function(result, request) {

            }
        });
    }

    SamplePanel = Ext.extend(Ext.DataView, {
        autoHeight: true,
        frame: true,
        cls: 'demos',
        itemSelector: 'dd',
        overClass: 'over',
        tpl: new Ext.XTemplate(
                '<div id="sample-ct">',
                '<tpl for=".">',
                '<div><a name="{id}"></a><h2><div>{title}</div></h2>',
                '<dl>',
                '<tpl for="samples">',
                '<dd ext:url="{url}" ext:id="{id}"><img src="{icon}"/>',
                '<div><h4>{text}',
                '<tpl if="this.isNew(values.status)">',
                '<span class="new-sample"> (New)</span>',
                '</tpl>',
                '<tpl if="this.isUpdated(values.status)">',
                '<span class="updated-sample"> (Updated)</span>',
                '</tpl>',
                '<tpl if="this.isExperimental(values.status)">',
                '<span class="new-sample"> (Experimental)</span>',
                '</tpl>',
                '</h4><p>{desc}</p></div>',
                '</dd>',
                '</tpl>',
                '<div style="clear:left"></div></dl></div>',
                '</tpl>',
                '</div>', {
            isExperimental: function(status) {
                return status == 'experimental';
            },
            isNew: function(status) {
                return status == 'new';
            },
            isUpdated: function(status) {
                return status == 'updated';
            }
        }),
        onDblClick: function(e) {
            var group = e.getTarget('h2', 3, true);
            if (group) {
                group.up('div').toggleClass('collapsed');
            } else {
                var t = e.getTarget('dd', 5, true);
                if (t && !e.getTarget('a', 2)) {
                    var url = t.getAttributeNS('ext', 'url');
                    var id = t.getAttributeNS('ext', 'id');
                    Ext.samples.RequestForm({id: id});
                }
            }
            return SamplePanel.superclass.onClick.apply(this, arguments);
        }
    });
    Ext.samples.SamplePanel = SamplePanel;
    Ext.reg('samplespanel', Ext.samples.SamplePanel);
})();

Ext.onReady(function() {
    (function() {

        var store = new Ext.data.JsonStore({
            url: Ext.SROOT + 'paneldeservicios/listaservicios',
            idProperty: 'id',
            fields: ['id', 'title', 'samples'],
            autoLoad: true
        });

        var panel = new Ext.Panel({
            title: 'Servicios',
            frame: true,
            id: 'all-demos',
            border: false,
            region: 'center',
            autoScroll: true,
            items: new SamplePanel({
                store: store
            }),
            tbar: ['->', {
                    iconCls: 'refresh',
                    tooltip: 'Recargar',
                    handler: function() {
                        store.reload();
                    }
                }]
        });

        new Ext.Viewport({
            layout: 'border',
            border: false,
            items: [panel, {
                    region: 'east',
                    title: 'Formulario de Solicitud',
                    id: 'form_request',
                    split: true,
                    layout: 'fit',
                    collapsible: true,
                    autoScroll: true,
                    width: 500,
                    minWidth: 400
                }]
        });

    }).defer(500);
});
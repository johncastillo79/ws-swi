/**
 * Sitmax ExtJS UI
 * Copyright(c) 2011-2012 ICG Inc.
 * @author Johns Castillo Valencia
 */
Ext.ns('domain.UserManager');

//Iframe Panel version 2.0
Ext.IframePanel = Ext.extend(Ext.Panel, {
    name: 'iframe',
    iframe: null,
    src: Ext.isIE && Ext.isSecure ? Ext.SSL_SECURE_URL : 'about:blank',
    maskMessage: 'Cargando ...',
    doMask: true,
    // component build
    initComponent: function() {
        this.bodyCfg = {
            tag: 'iframe',
            frameborder: '0',
            src: this.src,
            name: this.name
        }
        Ext.apply(this, {
        });
        Ext.IframePanel.superclass.initComponent.apply(this, arguments);

        // apply the addListener patch for 'message:tagging'
        this.addListener = this.on;

    },
    onRender: function() {
        Ext.IframePanel.superclass.onRender.apply(this, arguments);
        this.iframe = Ext.isIE ? this.body.dom.contentWindow : window.frames[this.name];
        this.body.dom[Ext.isIE ? 'onreadystatechange' : 'onload'] = this.loadHandler.createDelegate(this);
    },
    loadHandler: function() {
        this.src = this.body.dom.src;
        this.removeMask();
    },
    getIframe: function() {
        return this.iframe;
    },
    getUrl: function() {
        return this.body.dom.src;
    },
    setUrl: function(source) {
        this.setMask();
        this.body.dom.src = source;
    },
    resetUrl: function() {
        this.setMask();
        this.body.dom.src = this.src;
    },
    refresh: function() {
        if (!this.isVisible()) {
            return;
        }
        this.setMask();
        this.body.dom.src = this.body.dom.src;
    },
    /** @private */
    setMask: function() {
        if (this.doMask) {
            this.el.mask(this.maskMessage);
        }
    },
    removeMask: function() {
        if (this.doMask) {
            this.el.unmask();
        }
    }
});
Ext.reg('iframepanel', Ext.IframePanel);

domain.errors = {
    mustSelect: function() {
        Ext.MessageBox.show({
            title: 'Aviso',
            msg: 'Debe seleccionar un <b>Registro</b>.',
            buttons: Ext.MessageBox.OK,
            icon: Ext.Msg.INFO
        });
    },
    mustBeServer: function() {
        Ext.MessageBox.show({
            title: 'Aviso',
            msg: 'Debe seleccionar la <b>Ra&iacute;z del servicio</b>.',
            buttons: Ext.MessageBox.OK,
            icon: Ext.Msg.INFO
        });
    },
    mustBeOperation: function() {
        Ext.MessageBox.show({
            title: 'Aviso',
            msg: 'Debe seleccionar una <b>Operacion del servicio</b>.',
            buttons: Ext.MessageBox.OK,
            icon: Ext.Msg.INFO
        });
    },
    submitFailure: function(title, msg) {
        Ext.MessageBox.show({
            title: title,
            msg: msg,
            buttons: Ext.MessageBox.OK,
            icon: Ext.Msg.ERROR
        });
    },
    operationError: function(title, msg) {
        Ext.MessageBox.show({
            title: title,
            msg: msg,
            buttons: Ext.MessageBox.OK,
            icon: Ext.Msg.ERROR
        });
    },
    noData: function() {
        Ext.MessageBox.show({
            title: 'Error',
            msg: 'Error del servidor, no hay datos',
            buttons: Ext.MessageBox.OK,
            icon: Ext.Msg.ERROR
        });
    }
};

domain.ServiceManager = {
    processor: function(json) {
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
    },
    Fields: function(data, gridcfg) {
        if (data.length > 0) {
            var cols = new Array();

            var fields = new Array();
            for (var prop in data[0]) {
                fields.push({
                    name: prop
                })
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
                cols = gridcfg;
            }
            var grid = new Ext.grid.GridPanel({
                title: 'Resultados',
                height: 300,
                selModel: new Ext.grid.RowSelectionModel({singleSelect: true}),
                store: new Ext.data.JsonStore({
                    fields: fields,
                    data: data,
                    autoLoad: true
                }),
                columns: cols
            });
            return {grid: grid, cols: cols};
        }
        return null;
    },
    gridFields: function(data, id) {
        if (data.length > 0) {
            function moveSelectedRow(grid, direction) {
                var record = grid.getSelectionModel().getSelected();
                if (!record) {
                    return;
                }
                var index = grid.getStore().indexOf(record);
                if (direction < 0) {
                    index--;
                    if (index < 0) {
                        return;
                    }
                } else {
                    index++;
                    if (index >= grid.getStore().getCount()) {
                        return;
                    }
                }
                grid.getStore().remove(record);
                grid.getStore().insert(index, record);
                grid.getSelectionModel().selectRow(index, true);
            };

            var grid = new Ext.grid.EditorGridPanel({
                height: 300,
                selModel: new Ext.grid.RowSelectionModel({singleSelect: true}),
                store: new Ext.data.JsonStore({
                    fields: ['header', 'dataIndex', 'hidden', 'width', 'sortable'],
                    data: data,
                    autoLoad: true
                }),
                columns: [{
                        header: 'Campo', dataIndex: 'dataIndex', width: 170
                    }, {
                        header: 'Etiqueta (Editable)', dataIndex: 'header', width: 170,
                        editor: new Ext.form.TextField({
                            allowBlank: false
                        })
                    }, {
                        header: 'Ancho (Editable)', dataIndex: 'width', width: 100,
                        editor: new Ext.form.NumberField({
                            allowBlank: false
                        })
                    }, {
                        xtype: 'checkcolumn', header: 'Oculto', dataIndex: 'hidden'
                    }],
                tbar: [{
                        text: 'Guardar',
                        iconCls: 'entity-save',
                        handler: function() {
                            grid.getView().refresh();
                            var store = grid.getStore();
                            var source = new Array();
                            Ext.each(store.data.items, function(item) {
                                if (!item.data.hidden) {
                                    item.data.hidden = false;
                                }
                                source.push(item.data);
                            });
                            Ext.Ajax.request({
                                url: Ext.SROOT + 'individual/setgridcols',
                                method: 'POST',
                                params: {
                                    id: id,
                                    config: Ext.util.JSON.encode(source)
                                },
                                success: function(result, request) {
                                    win.close();
                                    Ext.MessageBox.show({
                                        title: 'Aviso',
                                        msg: 'Se ha guardado correctamente. Vuelva a ejecutar la operaci&oacute;n antes de volver a configurar las columnas.',
                                        buttons: Ext.MessageBox.OK,
                                        icon: Ext.Msg.WARNING
                                    });
                                },
                                failure: function(result, request) {

                                }
                            });
                        }
                    }, '-', {
                        iconCls: 'arrow-up',
                        tooltip: 'Subir',
                        handler: function() {
                            moveSelectedRow(grid, -1);
                        }
                    }, {
                        iconCls: 'arrow-down',
                        tooltip: 'Bajar',
                        handler: function() {
                            moveSelectedRow(grid, 1);
                        }
                    }]
            });

            var win = new Ext.Window({
                title: 'Configurar columnas',
                iconCls: 'settings',
                autoScroll: true,
                height: 300,
                width: 600,
                layout: 'fit',
                items: [grid],
                modal: true,
                buttons: [{
                        text: 'Cerrar',
                        handler: function() {
                            win.close();
                        }
                    }]
            });
            win.show();
        }
        return null;
    },
    definirServicio: function(options) {
        var form = new Ext.FormPanel({
            url: Ext.SROOT + 'definirservicio',
            border: false,
            autoHeight: true,
            fileUpload: true,
            bodyStyle: 'padding:10px',
            defaults: {
                msgTarget: 'side',
                width: 300
            },
            items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Nombre',
                    allowBlank: false,
                    name: 'nombre'
                }, {
                    xtype: 'textarea',
                    fieldLabel: 'Descripci&oacute;n',
                    allowBlank: false,
                    name: 'descripcion'
                }, {
                    xtype: 'fileuploadfield',
                    emptyText: 'Seleccione una imagen',
                    fieldLabel: 'Imagen',
                    name: 'imagen',
                    buttonText: '',
                    buttonCfg: {
                        iconCls: 'upload-icon'
                    }
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'Xpath',
                    allowBlank: false,
                    value: options.xpath ? options.xpath : '/',
                    name: 'responseXpath'
                }, {
                    xtype: "hidden",
                    name: "router",
                    value: options.router,
                }, {
                    xtype: "hidden",
                    name: "url",
                    value: options.url,
                }]
        });

        var win = new Ext.Window({
            title: 'Definir Servicio',
            autoScroll: true,
            autoHeight: true,
            width: 500,
            activeItem: 0,
            layout: 'anchor',
            items: [{
                    xtype: 'panel',
                    bodyStyle: 'padding:10px;background-color:#FFFFFF;color:#777777',
                    html: '<b>Esta acci&oacute;n establece la Operaci&oacute;n Web Service como servicio del sistema.  El Servicio requiere configuraci&oacute;n.</b>',
                    height: 50,
                    border: false
                }, form],
            modal: true,
            buttonAlign: 'center',
            buttons: [{
                    text: 'Guardar',
                    handler: function() {
                        form.getForm().submit({
                            success: function(form, action) {
                                options.grid.store.reload();
                                win.close();
                            },
                            failure: function(form, action) {
                                if (action.failureType === 'server') {
                                    var r = Ext.util.JSON.decode(action.response.responseText);
                                    com.icg.errors.submitFailure('Error', r.message);
                                }
                            }
                        });
                    }
                }]
        });
        win.show();
    },
    deleteService: function(options) {
        if (options.node.attributes.iconCls === 'server') {
            var item = options.node.attributes;
            Ext.MessageBox.confirm('Confirmar', '¿Confirma eliminar el registro? Se perderan Datos.', function(r) {
                if (r === 'yes') {
                    Ext.Ajax.request({
                        url: Ext.SROOT + 'eliminarservidor',
                        method: 'POST',
                        params: {
                            id: item.id
                        },
                        success: function(result, request) {
                            options.tree.getRootNode().reload();
                        },
                        failure: function(result, request) {

                        }
                    });
                }
            });
        } else {
            domain.errors.mustBeServer();
        }
    },
    reloadService: function(options) {
        if (options.node.attributes.iconCls === 'server') {
            var item = options.node.attributes;
            options.tree.getEl().mask("Procesando...", "x-mask-loading");
            Ext.Ajax.request({
                url: Ext.SROOT + 'reloadservidor',
                method: 'POST',
                params: {
                    id: item.id
                },
                success: function(result, request) {
                    options.tree.getRootNode().reload();
                    options.tree.getEl().unmask();
                    //options.node.expand(true, true);
                },
                failure: function(result, request) {
                    options.tree.getEl().unmask();
                }
            });
        } else {
            domain.errors.mustBeServer();
        }
    },
    openWsdl: function(options) {
        if (options.node.attributes.iconCls === 'server') {
            var item = options.node.attributes;
            options.panelinfo.removeAll();
            Ext.Ajax.request({
                url: Ext.SROOT + 'servidor/' + item.id,
                method: 'GET',
                success: function(result, request) {
                    var s = Ext.util.JSON.decode(result.responseText);
                    var tb = [{
                            xtype: 'displayfield',
                            value: 'WSDL'
                        }, {
                            xtype: 'textfield',
                            width: 700,
                            value: s.servidor.wsdlurl
                        }, {
                            text: 'Ir',
                            handler: function() {
                                //window.location.reload();
                                //setTimeout('window.location.reload()', 1);
                            }
                        }];
                    var cfg = {
                        id: s.servidor.id,
                        url: s.servidor.wsdlurl,
                        title: s.servidor.nombre + ' - WSDL',
                        tbar: tb
                    };
                    //top.swi.ui.openModule(cfg);
                    //window.open(s.servidor.wsdlurl, '', 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,width=800,height=530,screenX=50,screenY=50,top=50,left=200');
                    var siframe = new Ext.IframePanel({
                        src: s.servidor.wsdlurl,
                        tbar: [{
                                xtype: 'displayfield',
                                value: 'WSDL'
                            }, {
                                xtype: 'textfield',
                                width: 700,
                                readOnly: true,
                                value: s.servidor.wsdlurl
                            }, {
                                tooltip: 'Ir',
                                iconCls: 'play',
                                handler: function() {
                                    siframe.getIframe().location.reload();
                                }
                            }, '->', {
                                text: 'Abrir en nueva ventana',
                                iconCls: 'world',
                                handler: function() {
                                    var win = window.open(s.servidor.wsdlurl, '_blank');
                                    win.focus();
                                }
                            }]
                    });
                    options.panelinfo.add(siframe);
                    options.panelinfo.doLayout();
                },
                failure: function(result, request) {

                }
            });
        } else {
            domain.errors.mustBeServer();
        }
    },
    openOperation: function(options) {
        options.panelinfo.removeAll();
        if (options.node.attributes.iconCls === 'operation') {
            var item = options.node.attributes;
            Ext.Ajax.request({
                url: Ext.SROOT + 'formserviceitems',
                method: 'POST',
                params: {
                    id: item.id
                },
                success: function(result, request) {
                    var res = Ext.util.JSON.decode(result.responseText);
                    if (res.success) {
                        var sfields = res.data;
                        sfields.push({
                            xtype: 'hidden',
                            name: '__router_swi_var',
                            value: item.id
                        });
                        sfields.push({
                            xtype: 'hidden',
                            name: '__endpoint_swi_var',
                            value: item.url
                        });

                        var ppanel = new Ext.Panel({
                            xtype: 'panel',
                            title: 'Resultado',
                            region: 'center',
                            bodyStyle: 'padding:10px',
                            autoScroll: true,
                            height: 200,
                            tbar: [{
                                    text: 'Abrir XML',
                                    iconCls: 'open',
                                    handler: function() {
                                        window.open('cachexml', '', 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,copyhistory=no,width=800,height=530,screenX=50,screenY=50,top=50,left=200');
                                    }
                                }],
                            html: '<pre  class="brush: xml;"></pre>'
                        });

                        var form = new Ext.FormPanel({
                            url: Ext.SROOT + 'webserviceworld',
                            border: false,
                            autoHeight: true,
                            region: 'north',
                            split: true,
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
                                        options.panelinfo.getEl().mask("Procesando...", "x-mask-loading");
                                        ppanel.body.update('<pre class="brush: xml;"></pre>');
                                        Ext.Ajax.request({
                                            url: Ext.SROOT + 'webserviceworld',
                                            method: 'POST',
                                            params: form.getForm().getValues(),
                                            success: function(result, request) {
                                                options.panelinfo.getEl().unmask();
                                                var serviceResponse = Ext.util.JSON.decode(result.responseText);
                                                ppanel.body.update('<pre class="brush: xml;">' + serviceResponse.result + '</pre>');
                                            },
                                            failure: function(result, request) {
                                                options.panelinfo.getEl().unmask();

                                                Ext.MessageBox.show({
                                                    title: 'Error',
                                                    msg: 'Error del servidor',
                                                    buttons: Ext.MessageBox.OK,
                                                    icon: Ext.Msg.ERROR
                                                });
                                            }
                                        });
                                    }
                                }, '-', {
                                    text: 'Definir Servicio',
                                    iconCls: 'accept',
                                    tooltip: 'Definir como Servicio del sistema',
                                    handler: function() {
                                        domain.ServiceManager.definirServicio({
                                            router: item.id,
                                            url: item.url,
                                            grid: options.grid,
                                            xpath: Ext.getCmp('___xpath').getValue()
                                        });
                                    }
                                }, '-', {
                                    text: 'Ejecutar con XPath',
                                    iconCls: 'play',
                                    tooltip: 'Ejecupa la operaci&oacute;n aplicando XPath',
                                    handler: function() {
                                        options.panelinfo.getEl().mask("Procesando...", "x-mask-loading");
                                        ppanel.body.update('<pre class="brush: xml;"></pre>');
                                        var pparams = form.getForm().getValues();
                                        pparams['__xpath_swi_var'] = Ext.getCmp('___xpath').getValue();
//                                        console.log(pparams);
                                        if (Ext.getCmp('___xpath').getValue()) {
                                            Ext.Ajax.request({
                                                url: Ext.SROOT + 'webserviceworld1',
                                                method: 'POST',
                                                params: pparams,
                                                success: function(result, request) {
                                                    options.panelinfo.getEl().unmask();
                                                    var ro = Ext.util.JSON.decode(result.responseText);
                                                    if (ro.success) {
                                                        if (ro.result.length !== 0) {
                                                            ppanel.removeAll();
                                                            var data = domain.ServiceManager.processor(ro.result);
                                                            var rview = domain.ServiceManager.Fields(data, null);
                                                            if (rview) {
                                                                ppanel.add(rview.grid);
                                                                ppanel.doLayout();
                                                            } else {
                                                                domain.errors.noData();
                                                            }
                                                        } else {
                                                            domain.errors.noData();
                                                        }
                                                    } else {
                                                        domain.errors.submitFailure('error', ro.message);
                                                    }
                                                },
                                                failure: function(result, request) {
                                                    options.panelinfo.getEl().unmask();
                                                    Ext.MessageBox.show({
                                                        title: 'Error',
                                                        msg: 'Error del servidor',
                                                        buttons: Ext.MessageBox.OK,
                                                        icon: Ext.Msg.ERROR
                                                    });
                                                }
                                            });
                                        } else {
                                            Ext.MessageBox.show({
                                                title: 'Error',
                                                msg: 'Error al ejecutar XPath',
                                                buttons: Ext.MessageBox.OK,
                                                icon: Ext.Msg.ERROR
                                            });
                                        }
                                    }
                                }, {
                                    xtype: 'textfield',
                                    width: 300,
                                    value: '/',
                                    id: '___xpath'
                                }]
                        });

                        var panel = new Ext.Panel({
                            layout: 'border',
                            items: [form, ppanel]
                        });
                        options.panelinfo.add(panel);
                        options.panelinfo.doLayout();
                    } else {
                        domain.errors.operationError('Error', 'Error en el Servidor, No implementado');
                    }
                },
                failure: function(result, request) {
                    domain.errors.operationError('Error', 'Error en el Servidor');
                }
            });
        } else {
            domain.errors.mustBeOperation();
        }
    },
    datosServicio: function() {
        return {
            xtype: 'fieldset',
            title: 'Datos de conexion',
            defaults: {
                msgTarget: 'side',
                width: 450
            },
            items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Nombre',
                    allowBlank: false,
                    name: 'nombre'
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'WSDL URL',
                    allowBlank: false,
                    name: 'wsdlurl'
                }]
        };
    },
    newService: function(options) {
        var form = new Ext.FormPanel({
            url: Ext.SROOT + 'crearservicio',
            border: false,
            autoHeight: true,
            bodyStyle: 'padding:10px',
            labelWidth: 100,
            waitMsgTarget: true,
            items: [this.datosServicio()]
        });

        var win = new Ext.Window({
            iconCls: 'server',
            title: 'Registrar Servicio',
            autoScroll: true,
            width: 650,
            autoHeight: true,
            minWidth: 640,
            items: form,
            modal: true,
            buttonAlign: 'center',
            buttons: [{
                    text: 'Guardar',
                    handler: function() {
                        var button = this;
                        if (form.getForm().isValid()) {
                            button.disabled = true;
                            form.getForm().submit({
                                waitMsg: 'Leyendo WSDL...',
                                success: function(form, action) {
                                    options.tree.getRootNode().reload();
                                    win.close();
                                },
                                failure: function(form, action) {
                                    if (action.response.isAbort || action.response.isTimeout) {
                                        domain.errors.submitFailure('Error local', 'Abortado! fallo en conectividad');
                                    } else {
                                        domain.errors.submitFailure('Error servidor', action.result.message);
                                    }
                                    button.disabled = false;
                                }
                            });
                        }
                    }
                }]
        });
        win.show();
    },
    deleteServiceDef: function(rec, grid) {
        Ext.MessageBox.confirm('Confirmar', '¿Confirma eliminar el registro? Se perderan Datos.', function(r) {
            if (r === 'yes') {
                Ext.Ajax.request({
                    url: Ext.SROOT + 'eliminarservicio',
                    method: 'POST',
                    params: {
                        id: rec.data.id
                    },
                    success: function(result, request) {
                        var r = Ext.util.JSON.decode(result.responseText);
                        if (r.success) {
                            grid.store.reload();
                        } else {
                            domain.errors.submitFailure('Error', r.message);
                        }
                    },
                    failure: function(result, request) {

                    }
                });
            }
        });
    },
    editServiceDef: function(options) {
        var form = new Ext.FormPanel({
            url: Ext.SROOT + 'actualizarservicio',
            border: false,
            autoHeight: true,
            fileUpload: true,
            bodyStyle: 'padding:10px',
            defaults: {
                msgTarget: 'side',
                width: 300
            },
            items: [{
                    xtype: 'textfield',
                    fieldLabel: 'Nombre',
                    allowBlank: false,
                    name: 'nombre'
                }, {
                    xtype: 'textarea',
                    fieldLabel: 'Descripci&oacute;n',
                    allowBlank: false,
                    name: 'descripcion'
                }, {
                    xtype: 'fileuploadfield',
                    emptyText: 'Seleccione una imagen',
                    fieldLabel: 'Imagen',
                    name: 'imagen',
                    buttonText: '',
                    buttonCfg: {
                        iconCls: 'upload-icon'
                    }
                }, {
                    xtype: 'textfield',
                    fieldLabel: 'Xpath',
                    allowBlank: false,
                    name: 'responseXpath'
                }, {
                    xtype: "hidden",
                    name: "router"
                }, {
                    xtype: "hidden",
                    name: "url"
                }, {
                    xtype: "hidden",
                    name: "id"
                }]
        });

        form.getForm().loadRecord(options.record);

        var win = new Ext.Window({
            title: 'Definir Servicio',
            autoScroll: true,
            autoHeight: true,
            width: 500,
            activeItem: 0,
            layout: 'anchor',
            items: [form],
            modal: true,
            buttonAlign: 'center',
            buttons: [{
                    text: 'Guardar',
                    handler: function() {
                        form.getForm().submit({
                            success: function(form, action) {
                                options.grid.store.reload();
                                win.close();
                            },
                            failure: function(form, action) {
                                if (action.failureType === 'server') {
                                    var r = Ext.util.JSON.decode(action.response.responseText);
                                    com.icg.errors.submitFailure('Error', r.errorMessage);
                                }
                            }
                        });
                    }
                }]
        });
        win.show();
    },
    requestForm: function(options) {
        var formreq = options.panelinfo;//Ext.getCmp('form_request');
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
                    layout: 'fit'
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
                                        if (ro.result.length !== 0) {
                                            ppanel.removeAll();
                                            var data = domain.ServiceManager.processor(ro.result);
                                            var rview = domain.ServiceManager.Fields(data, ro.gridcfg);
                                            if (rview) {
                                                var grid = rview.grid;
                                                ppanel.add(grid);
                                                var tb = new Ext.Toolbar();
                                                grid.add(tb);
                                                tb.add({
                                                    text: 'Configurar columnas',
                                                    iconCls: 'settings',
                                                    handler: function() {
                                                        domain.ServiceManager.gridFields(rview.cols, options.id);
                                                    }
                                                });

                                                ppanel.doLayout();
                                            } else {
                                                domain.errors.noData();
                                            }
                                        } else {
                                            domain.errors.noData();
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
};

domain.ServiceManager.View = {
    init: function() {

        var tree = new Ext.tree.TreePanel({
            title: 'Servidores',
            iconCls: 'server',
            region: 'north',
            autoScroll: true,
            height: 250,
            split: true,
            collapsible: true,
            animate: true,
            loadMask: true,
            rootVisible: false,
            root: {
                nodeType: 'async'
            },
            loader: new Ext.tree.TreeLoader({
                dataUrl: 'treeservices',
                requestMethod: 'GET'
            }),
            tbar: [{
                    iconCls: 'refresh',
                    tooltip: 'Recargar',
                    handler: function() {
                        tree.getRootNode().reload();
                    }
                }, '-', {
                    text: 'Archivo',
                    menu: {
                        items: [{
                                text: 'Nuevo servidor...',
                                iconCls: 'server-add',
                                tooltip: 'Nuevo servicio',
                                handler: function() {
                                    domain.ServiceManager.newService({tree: tree});
                                }
                            }, {
                                text: 'Eliminar servidor',
                                iconCls: 'server-delete',
                                tooltip: 'Elimina la fuete de informaci&oacute;n',
                                handler: function() {
                                    var record = tree.getSelectionModel().getSelectedNode();
                                    if (record) {
                                        domain.ServiceManager.deleteService({node: record, tree: tree});
                                    } else {
                                        domain.errors.mustSelect();
                                    }
                                }
                            }, '-', {
                                iconCls: 'page-refresh',
                                text: 'Recargar',
                                tooltip: 'Actualiza la estructura desde el servidor original',
                                handler: function() {
                                    var record = tree.getSelectionModel().getSelectedNode();
                                    if (record) {
                                        domain.ServiceManager.reloadService({node: record, tree: tree});
                                    } else {
                                        domain.errors.mustSelect();
                                    }
                                }
                            }, {
                                iconCls: 'open',
                                text: 'Abrir WSDL',
                                tooltip: 'Abrir WSDL',
                                handler: function() {
                                    var record = tree.getSelectionModel().getSelectedNode();
                                    if (record) {
                                        domain.ServiceManager.openWsdl({
                                            panelinfo: serviceInfoPanel,
                                            node: record,
                                            tree: tree
                                        });
                                    } else {
                                        domain.errors.mustSelect();
                                    }
                                }
                            }
                        ]}
                }, '-', {
                    iconCls: 'arrow_divide',
                    text: 'Expandir',
                    handler: function() {
                        tree.expandAll();
                    }
                }, {
                    iconCls: 'arrow_join',
                    text: 'Contraer',
                    handler: function() {
                        tree.collapseAll();
                    }
                }, '-', {
                    text: 'Abrir ejecutar',
                    iconCls: 'play',
                    tooltip: 'Abrir Operaci&oacute;n',
                    handler: function() {
                        var record = tree.getSelectionModel().getSelectedNode();
                        if (record) {
                            domain.ServiceManager.openOperation({
                                node: record,
                                tree: tree,
                                panelinfo: serviceInfoPanel,
                                grid: sgrid
                            });
                        } else {
                            domain.errors.mustSelect();
                        }
                    }
                }],
            listeners: {
                dblclick: function(node, e) {
                    if (node.attributes.iconCls === 'operation') {
                        domain.ServiceManager.openOperation({
                            node: node,
                            tree: tree,
                            panelinfo: serviceInfoPanel,
                            grid: sgrid
                        });
                    }
                }
            }
        });

        var serviceInfoPanel = new Ext.Panel({
            title: 'Informacion',
            region: 'center',
            layout: 'fit'
        });

        var sstore = new Ext.data.JsonStore({
            url: Ext.SROOT + 'individual/listaservicios',
            fields: [{name: 'id'},
                {name: 'nombre'}, {name: 'descripcion'}, {name: 'responseXpath'}, {name: 'router'}, {name: 'url'}],
            autoLoad: true
        });

        var cols = [new Ext.grid.RowNumberer({
                width: 27
            }),
            {header: "Nombre", width: 150, autoExpandColumn: true, sortable: true, dataIndex: 'nombre'},
            {header: "Descripci&oacute;n", width: 150, autoExpandColumn: true, sortable: true, dataIndex: 'descripcion'},
            {header: "Xpath", width: 150, autoExpandColumn: true, sortable: true, dataIndex: 'responseXpath'},
            {header: "Servidor", width: 150, autoExpandColumn: true, sortable: true, dataIndex: 'router', renderer: function(val) {
                    var id = val.split(':')[0], text;
                    Ext.each(tree.root.childNodes, function(n) {
                        if (n.attributes.id === id) {
                            text = n.attributes.text;
                        }
                    });
                    return text;
                }}
        ];

        var sgrid = new Ext.grid.GridPanel({
            store: sstore,
            height: 200,
            columns: cols,
            region: 'center',
            loadMask: true,
            selModel: new Ext.grid.RowSelectionModel({singleSelect: true}),
            title: 'Servicios definidos',
            tbar: [{
                    iconCls: 'refresh',
                    tooltip: 'Recargar',
                    handler: function() {
                        sgrid.store.reload();
                    }
                }, '-', {
                    text: 'Editar',
                    iconCls: 'update',
                    handler: function() {
                        var record = sgrid.getSelectionModel().getSelected();
                        if (record) {
                            domain.ServiceManager.editServiceDef({
                                record: record,
                                grid: sgrid
                            });
                        } else {
                            domain.errors.mustSelect();
                        }
                    }
                }, {
                    text: 'Eliminar',
                    iconCls: 'delete',
                    handler: function() {
                        var record = sgrid.getSelectionModel().getSelected();
                        if (record) {
                            domain.ServiceManager.deleteServiceDef(record, sgrid);
                        } else {
                            domain.errors.mustSelect();
                        }
                    }
                }, '-', {
                    text: 'Abrir ejecutar',
                    iconCls: 'play',
                    handler: function() {
                        var record = sgrid.getSelectionModel().getSelected();
                        if (record) {
                            domain.ServiceManager.requestForm({
                                panelinfo: serviceInfoPanel,
                                id: record.data.id
                            });
                        } else {
                            domain.errors.mustSelect();
                        }
                    }
                }]
        });


        var izquierda = new Ext.Panel({
            layout: 'border',
            region: 'west',
            collapsible: true,
            split: true,
            width: 450,
            minWidth: 400,
            maxWidth: 550,
            items: [tree, sgrid]
        });

        new Ext.Viewport({
            layout: 'fit',
            border: false,
            items: new Ext.Panel({
                border: false,
                layout: 'border',
                items: [izquierda, serviceInfoPanel]
            })
        });
    }
};

Ext.onReady(domain.ServiceManager.View.init, domain.ServiceManager.View);
<%--
    Document   : Vista del RPI
    Created on : 30-11-2013, 07:05:10 PM
    Author     : John Castillo Valencia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<!DOCTYPE HTML>
<html lang="es">
    <head>
        <title>Vista del RPI</title>
        <!-- ALL ExtJS Framework resources -->
        <%@include file="../ExtJSScripts-ES.jsp"%>  

        <link type="text/css" rel="stylesheet" href="libs/fe/css/FileBrowserPanel.css">
        <script type="text/javascript" src="<c:url value="libs/fe/Ext.ux.FileBrowserPanel.js"/>"></script> 

        <script type="text/javascript">
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

            Ext.namespace('domain');
            domain.errors = {
                mustSelect: function() {
                    Ext.MessageBox.show({
                        title: 'Aviso',
                        msg: 'Debe seleccionar un <b>Registro</b>.',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.Msg.INFO
                    });
                }
            };

            domain.Manager = {
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
                fields: function(data, gridcfg, sm) {
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

                        var gconfig = {
                            store: new Ext.data.JsonStore({
                                fields: fields,
                                data: data,
                                autoLoad: true
                            }),
                            columns: cols
                        };

                        if (sm) {
                            gconfig.sm = sm;
                        }

                        var grid = new Ext.grid.GridPanel(gconfig);
                        return grid;
                    }
                    return null;
                },
                individual: function(options) {
                    var serviceResponse;
                    var grid;
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
                                            ppanel.removeAll();
                                            ppanel.getEl().mask("Procesando...", "x-mask-loading");
                                            form.getForm().submit({
                                                success: function(form, action) {
                                                    ppanel.getEl().unmask();
                                                    serviceResponse = Ext.util.JSON.decode(action.response.responseText);
                                                    if (serviceResponse.gridcfg) {
                                                        serviceResponse.gridcfg = Ext.util.JSON.decode(serviceResponse.gridcfg);
                                                    }
                                                    win.getEl().unmask();
                                                    var sm = new Ext.grid.CheckboxSelectionModel();
                                                    var data = domain.Manager.processor(serviceResponse.result);
                                                    grid = domain.Manager.fields(data, serviceResponse.gridcfg, sm);
                                                    ppanel.add(grid);
                                                    ppanel.doLayout();
                                                },
                                                failure: function(form, action) {
                                                    ppanel.getEl().unmask();
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
                                        text: 'Usar resultado',
                                        iconCls: 'accept',
                                        handler: function() {
                                            if (serviceResponse) {
                                                var resPanel = Ext.getCmp('responsepanel-' + serviceResponse.id);
                                                //resPanel.body.update('<pre>' + serviceResponse.result + '</pre>');
                                                resPanel.removeAll();
                                                if (grid.getSelectionModel().getSelections().length > 0) {
                                                    //console.log(grid.getSelectionModel().getSelections());
                                                    var ndata = new Array();
                                                    Ext.each(grid.getSelectionModel().getSelections(), function(rec) {
                                                        ndata.push(rec.data);
                                                    });
                                                    var ngrid = domain.Manager.fields(ndata, serviceResponse.gridcfg);
                                                    resPanel.add(ngrid);
                                                    resPanel.doLayout();
                                                    options.salida.result = ndata;
                                                    options.salida.redefinido = true;
                                                    win.close();
                                                } else {
                                                    Ext.MessageBox.show({
                                                        title: 'Error',
                                                        msg: 'Seleccione registros',
                                                        buttons: Ext.MessageBox.OK,
                                                        icon: Ext.Msg.ERROR
                                                    });
                                                }
                                            } else {
                                                Ext.MessageBox.show({
                                                    title: 'Error',
                                                    msg: 'Seleccione registros',
                                                    buttons: Ext.MessageBox.OK,
                                                    icon: Ext.Msg.ERROR
                                                });
                                            }
                                        }
                                    }]
                            });
                            var ppanel = new Ext.Panel({
                                xtype: 'panel',
                                title: 'Resultado',
                                region: 'center',
                                layout: 'fit',
                                height: 200
                            });
                            var win = new Ext.Window({
                                title: 'Definir Respuesta',
                                autoScroll: true,
                                layout: 'border',
                                width: 600,
                                height: 380,
                                minHeight: 250,
                                minWidth: 550,
                                items: [form, ppanel],
                                maximizable: true,
                                modal: true,
                                buttons: [{
                                        text: 'Cerrar',
                                        handler: function() {
                                            win.close();
                                        }
                                    }]
                            });
                            win.show();
                        },
                        failure: function(result, request) {

                        }
                    });
                },
                guardarComo: function(options) {
                    var form = new Ext.FormPanel({
                        url: Ext.SROOT + 'rpiview/guardarcomorpi',
                        border: false,
                        autoHeight: true,
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
                                xtype: "hidden",
                                name: "entrada",
                                value: Ext.util.JSON.encode(options.output.entrada),
                            }, {
                                xtype: "hidden",
                                name: "salida",
                                value: Ext.util.JSON.encode(options.output.salida),
                            }]
                    });

                    var win = new Ext.Window({
                        iconCls: 'entity-save',
                        title: 'Guardar como...',
                        autoScroll: true,
                        autoHeight: true,
                        width: 500,
                        activeItem: 0,
                        layout: 'anchor',
                        items: form,
                        modal: true,
                        buttonAlign: 'center',
                        buttons: [{
                                text: 'Guardar',
                                handler: function() {
                                    form.getForm().submit({
                                        success: function(form, action) {
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
                guardar: function(options) {
                    console.log(options.output);
                    var form = new Ext.FormPanel({
                        url: Ext.SROOT + 'rpiview/guardarrpi',
                        border: false,
                        autoHeight: true,
                        bodyStyle: 'padding:10px',
                        defaults: {
                            msgTarget: 'side',
                            width: 300
                        },
                        items: [{
                                xtype: 'hidden',
                                allowBlank: false,
                                name: 'id',
                                value: options.output.id
                            }, {
                                xtype: "hidden",
                                name: "entrada",
                                value: Ext.util.JSON.encode(options.output.entrada),
                            }, {
                                xtype: "hidden",
                                name: "salida",
                                value: Ext.util.JSON.encode(options.output.salida),
                            }]
                    });

                    form.getForm().submit({
                        success: function(form, action) {
                            //win.close();
                        },
                        failure: function(form, action) {
                            if (action.failureType === 'server') {
                                var r = Ext.util.JSON.decode(action.response.responseText);
                                com.icg.errors.submitFailure('Error', r.errorMessage);
                            }
                        }
                    });

                },
                mustExecute: function() {
                    Ext.MessageBox.show({
                        title: 'Aviso',
                        msg: 'Debe ejecutar la <b>consulta</b>.',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.Msg.INFO
                    });
                },
                imprimir: function() {
                    var fb = new Ext.ux.FileBrowserPanel({
                        //width: 800,
                        //height: 600			
                    });

                    var store = new Ext.data.JsonStore({
                        url: Ext.SROOT + 'rpiview/listarpis',
                        //root: 'data',
                        fields: ['id', 'nombre', 'usuario'],
                        autoLoad: true
                    });

                    var grid = new Ext.grid.GridPanel({
                        //title: 'Usuarios',
                        border: false,
                        store: store,
                        loadMask: true,
                        columns: [new Ext.grid.RowNumberer({
                                width: 27
                            }), {
                                header: "Archivo",
                                sortable: true,
                                width: 100,
                                dataIndex: 'nombre'
                            }, {
                                header: "Usuario",
                                sortable: true,
                                dataIndex: 'usuario'
                            }, {
                                header: "Fecha",
                                sortable: true,
                                dataIndex: 'fecha'
                            }
                        ]});
                    var win = new Ext.Window({
                        iconCls: 'entity-save',
                        title: 'Archivos',
                        autoScroll: true,
                        //autoHeight: true,
                        width: 500,
                        height: 400,
                        //activeItem: 0,
                        layout: 'fit',
                        items: grid,
                        modal: true,
                        //buttonAlign: 'center',
                        buttons: [{
                                text: 'Cerrar',
                                handler: function() {
                                    win.close();
                                }
                            }, {
                                text: 'Imprimir',
                                iconCls: 'printer',
                                handler: function() {
                                    var record = grid.getSelectionModel().getSelected();
                                    if (record) {
                                        //options.record = record;
                                        //domain.UserManager.changePassword(options);
                                        //alert(record.data.id);
                                        window.location = '/ReportWebUIF/rpi_report?' + record.data.id;
                                    } else {
                                        Ext.MessageBox.show({
                                            title: 'Aviso',
                                            msg: 'Debe seleccionar un <b>Registro</b>.',
                                            buttons: Ext.MessageBox.OK,
                                            icon: Ext.Msg.INFO
                                        });
                                    }
                                }
                            }]
                    });
                    win.show();
                },
                openFile: function(options) {
                    var storeUsuario = new Ext.data.JsonStore({
                        url: 'rpiview/listarusuarios',
                        root: 'data',
                        fields: [{name: 'id'},
                            {name: 'usuario'}
                        ],
                        autoLoad: true
                    });

                    var cboBusUsuario = new Ext.form.ComboBox({
                        fieldLabel: 'Usuario',
                        name: 'usuario',
                        forceSelection: true,
                        store: storeUsuario,
                        allowBlank: false,
                        triggerAction: 'all',
                        lastQuery: '', //hideTrigger:true,
                        editable: false,
                        displayField: 'usuario',
                        valueField: 'id',
                        //value: '[TODOS]',
                        typeAhead: true,
                        selectOnFocus: true
                    });

                    var dateField1 = new Ext.form.DateField({
                        emptyText: 'de...',
                        id: '_start_date_',
                        name: 'fecha1',
                        format: 'd/m/Y',
                        width: 165,
                        value: new Date(),
                        allowBlank: false,
                        endDateField: '_end_date_'
                    });

                    var dateField2 = new Ext.form.DateField({
                        emptyText: 'a...',
                        id: '_end_date_',
                        name: 'fecha2',
                        format: 'd/m/Y',
                        width: 165,
                        value: new Date(),
                        allowBlank: false,
                        startDateField: '_start_date_'
                    });

                    var form = new Ext.FormPanel({
                        border: false,
                        url: Ext.SROOT + 'rpiview/listarpis',
                        region: 'north',
                        autoHeight: true,
                        bodyStyle: 'padding:10px',
                        frame: true,
                        collapsible: true,
                        buttonAlign: 'center',
                        items: [cboBusUsuario,
                            {xtype: 'compositefield',
                                fieldLabel: 'Periodo',
                                items: [dateField1, dateField2]
                            }],
                        buttons: [{
                                text: 'Buscar',
                                handler: function() {
                                    if (form.getForm().isValid()) {
                                        store.load({params: form.getForm().getValues()})
                                    }
                                }
                            }]
                    });

                    var store = new Ext.data.JsonStore({
                        url: Ext.SROOT + 'rpiview/listarpis',
                        fields: ['id', 'nombre', 'usuario', 'fecha', 'entrada', 'salida']
                    });

                    var grid = new Ext.grid.GridPanel({
                        //title: 'Usuarios',
                        border: false,
                        region: 'center',
                        store: store,
                        loadMask: true,
                        columns: [new Ext.grid.RowNumberer({
                                width: 27
                            }), {
                                header: "Archivo",
                                sortable: true,
                                width: 100,
                                dataIndex: 'nombre'
                            }, {
                                header: "Usuario",
                                sortable: true,
                                dataIndex: 'usuario'
                            }, {
                                header: "Fecha",
                                sortable: true,
                                dataIndex: 'fecha'
                            }
                        ]});
                    var win = new Ext.Window({
                        title: 'Abrir archivo...',
                        autoScroll: true,
                        width: 600,
                        height: 400,
                        layout: 'border',
                        items: [form, grid],
                        modal: true,
                        buttons: [{
                                text: 'Abrir',
                                handler: function() {
                                    var record = grid.getSelectionModel().getSelected();
                                    console.log(record);
                                    if (record) {
                                        options.ptitle.setTitle('Formulario de Consulta - (' + record.data.nombre + ')');
                                        var fdata = Ext.util.JSON.decode(record.data.entrada);
                                        options.output.entrada = fdata;
                                        var input = new Object();
                                        Ext.each(fdata, function(e) {
                                            input[e.name] = e.value;
                                        });
                                        options.form.getForm().loadRecord({data: input});

                                        var rdata = Ext.util.JSON.decode(record.data.salida);
                                        options.output.salida = rdata;
                                        console.log(rdata);
                                        for (var prop in rdata) {
                                            var robj = rdata[prop];
                                            var resPanel = Ext.getCmp('responsepanel-' + robj.id);
                                            console.log(robj.id);
                                            if (resPanel) {
                                                var gridx = domain.Manager.fields(robj.result, robj.gridcfg);
                                                resPanel.removeAll();
                                                resPanel.add(gridx);
                                                resPanel.doLayout();
                                            }
                                        }

                                        options.output.id = record.data.id;
                                        options.output.nombre = record.data.nombre;
                                        Ext.getCmp('__rpisave').setDisabled(false);
                                        Ext.getCmp('__rpisaveas').setDisabled(false);
                                        Ext.getCmp('__rpiprint').setDisabled(false);
                                        Ext.getCmp('__rpiprintxls').setDisabled(false);

                                        win.close();
                                    } else {
                                        domain.errors.mustSelect();
                                    }
                                }
                            }, {
                                text: 'Cerrar',
                                handler: function() {
                                    win.close();
                                }
                            }]
                    });
                    win.show();
                },
                getEntrada: function(form, values) {
                    var a = new Array();
                    for (var prop in values) {
                        a.push({
                            name: prop,
                            value: values[prop],
                            label: form.getForm().findField(prop).fieldLabel
                        });
                    }
                    return a;
                },
                print: function(id, file) {
                      if(file === 'PDF') {
                      window.location =  '/ReportWebUIF/rpi_report?id='+id;
                      }
                      if(file === 'XLS') {
                      window.location =  '/ReportWebUIF/rpi_report?id='+id + '&format=excel';
                      }
                      
//                    var siframe = new Ext.IframePanel({
//                        src: '/ReportWebUIF/rpi_report?id='+id
//                    });
//                    var win = new Ext.Window({
//                        title: 'Imprimir archivo...',
//                        autoScroll: true,
//                        width: 800,
//                        height: 500,
//                        layout: 'fit',
//                        items: [siframe],
//                        modal: true,
//                        buttons: [{
//                                text: 'Cerrar',
//                                handler: function() {
//                                    win.close();
//                                }
//                            }]
//                    });
//                    win.show();
                }
            };

            domain.Panel = {
                init: function() {
                    var fileOpen = null;

                    //RPI DATA
                    var _rpidata = new Object();

                    _rpidata.salida = new Object(); // = new Array();
                    //var isEjecutado = false;

                    var formParametro = new Ext.FormPanel({
                        url: Ext.SROOT + 'rpiwsrwquest',
                        border: false,
                        autoHeight: true,
                        bodyStyle: 'padding:10px',
                        labelWidth: 100,
                        frame: false,
                    });

                    var formServicio = new Ext.Panel({
                        border: false,
                        items: [formParametro],
                        tbar: [{
                                text: 'Ejecutar',
                                iconCls: 'play',
                                handler: function() {
                                    _rpidata.entrada = domain.Manager.getEntrada(formParametro, formParametro.getForm().getValues());
                                    //_rpidata.salida = new Array();
                                    //isEjecutado = true;
                                    Ext.each(servicesdata, function(s) {
                                        var params = new Object();
                                        Ext.each(s.parametros, function(p) {
                                            if (!p.rpifield) {
                                                params[p.nombre] = formParametro.getForm().findField(s.id + ':' + p.nombre).getValue();
                                            } else {
                                                params[p.nombre] = formParametro.getForm().findField('rpifield-' + p.rpifield).getValue();
                                            }
                                        });
                                        params['_swi_userservice_id_'] = s.id;
                                        Ext.getCmp('responsepanel-' + s.id).getEl().mask("Procesando...", "x-mask-loading");
                                        Ext.Ajax.request({
                                            url: Ext.SROOT + 'webservicesystem',
                                            method: 'POST',
                                            params: params,
                                            success: function(result, request) {
                                                var robj = Ext.util.JSON.decode(result.responseText);
                                                if (robj.success) {
                                                    _rpidata.salida['srv-' + s.id] = new Object();
                                                    _rpidata.salida['srv-' + s.id].id = s.id;
                                                    if (robj.gridcfg) {
                                                        robj.gridcfg = Ext.util.JSON.decode(robj.gridcfg);
                                                        _rpidata.salida['srv-' + s.id].gridcfg = robj.gridcfg;
                                                    }
                                                    //_rpidata.salida['srv-' + s.id] = new Object();
                                                    var resPanel = Ext.getCmp('responsepanel-' + robj.id);
                                                    resPanel.getEl().unmask();
                                                    var data = domain.Manager.processor(robj.result);
                                                    _rpidata.salida['srv-' + s.id].result = data;
                                                    var grid = domain.Manager.fields(data, robj.gridcfg);
                                                    resPanel.removeAll();
                                                    resPanel.add(grid);
                                                    resPanel.doLayout();
                                                } else {
                                                    var resPanel = Ext.getCmp('responsepanel-' + s.id);
                                                    resPanel.getEl().unmask();
                                                    resPanel.body.update('<span style="color:red"><b>Error del servidor...!</b></span>');
                                                }
                                            },
                                            failure: function(result, request) {
                                                var respanel = Ext.getCmp('responsepanel-' + s.id);
                                                respanel.getEl().unmask();
                                                respanel.body.update('<span style="color:red"><b>Error del servidor...!</b></span>');
                                                //Error unknow
                                            }
                                        });
                                    });
                                    Ext.getCmp('__rpisaveas').setDisabled(false);
                                }
                            }, '-', {
                                text: 'Archivo',
                                menu: {
                                    items: [{
                                            text: 'Abrir...',
                                            iconCls: 'open',
                                            handler: function() {
                                                domain.Manager.openFile({
                                                    form: formParametro,
                                                    resultados: derecha,
                                                    output: _rpidata,
                                                    ptitle: centro
                                                });
                                            }
                                        }, {
                                            text: 'Guardar',
                                            iconCls: 'entity-save',
                                            id: '__rpisave',
                                            disabled: true,
                                            handler: function() {
                                                Ext.MessageBox.confirm('Confirmar', '¿Confirma sobreescribir?.', function(r) {
                                                    if (r === 'yes') {
                                                        domain.Manager.guardar({
                                                            output: _rpidata
                                                        });
                                                    }
                                                });
                                            }
                                        }, {
                                            text: 'Guardar como...',
                                            iconCls: 'entity-save',
                                            disabled: true,
                                            id: '__rpisaveas',
                                            handler: function() {
                                                if (_rpidata) {
                                                    domain.Manager.guardarComo({
                                                        output: _rpidata
                                                    });
                                                } else {
                                                    domain.Manager.mustExecute();
                                                }
                                            }
                                        }, '-', {
                                            text: 'Imprimir PDF',
                                            iconCls: 'printer',
                                            id: '__rpiprint',
                                            disabled: true,
                                            handler: function() {
                                                //alert(_rpidata.id);
                                                //domain.Manager.imprimir({});
                                                if (_rpidata.id) {
                                                    domain.Manager.print(_rpidata.id, 'PDF');
                                                }
                                            }
                                        }, {
                                            text: 'Imprimir MS Excel',
                                            iconCls: 'printer',
                                            id: '__rpiprintxls',
                                            disabled: true,
                                            handler: function() {
                                                //alert(_rpidata.id);
                                                //domain.Manager.imprimir({});
                                                if (_rpidata.id) {
                                                    domain.Manager.print(_rpidata.id, 'XLS');
                                                }
                                            }
                                        }]
                                }
                            }, {
                                text: 'Limpiar',
                                tooltip: 'Limpiar todo',
                                iconCls: 'delete',
                                handler: function() {

                                }
                            }, '->', {
                                tooltip: 'Actualizar Formulario',
                                iconCls: 'refresh',
                                handler: function() {
                                    fsloadRpi();
                                    isEjecutado = false;
                                }
                            }]
                    });

                    var centro = new Ext.Panel({
                        title: 'Formulario de Consulta',
                        region: 'center',
                        items: [formServicio]
                    });

                    var derecha = new Ext.Panel({
                        title: 'Resultados',
                        region: 'east',
                        split: true,
                        autoScroll: true,
                        width: 700,
                        minWidth: 300,
                        maxWidth: 900,
                        layout: 'anchor',
                        items: []
                    });

                    new Ext.Viewport({
                        layout: 'fit',
                        border: false,
                        items: [{
                                layout: 'border',
                                border: false,
                                items: [centro, derecha]
                            }
                        ]
                    });

                    //Lista de servicios del usuario
                    var servicesdata = null;
                    Ext.Ajax.request({
                        url: Ext.SROOT + 'rpiview/listaserviciosusuario',
                        method: 'GET',
                        success: function(result, request) {
                            servicesdata = Ext.util.JSON.decode(result.responseText);
                            //Each UserServices
                            Ext.each(servicesdata, function(s) {
                                derecha.add({
                                    xtype: 'panel',
                                    title: s.nombre,
                                    layout: 'fit',
                                    id: 'responsepanel-' + s.id,
                                    //bodyStyle: 'padding:10px',
                                    autoScroll: true,
                                    collapsible: true,
                                    height: 170,
                                    tbar: ['->', {
                                            text: 'Definir',
                                            tooltip: 'Definir la respuesta del servicio',
                                            iconCls: 'accept',
                                            handler: function() {
                                                if (!_rpidata.salida['srv-' + s.id]) {
                                                    _rpidata.salida['srv-' + s.id] = new Object();
                                                }
                                                domain.Manager.individual({
                                                    id: s.id,
                                                    salida: _rpidata.salida['srv-' + s.id]
                                                });
                                            }
                                        }]
                                });
                            });
                            derecha.doLayout();
                        },
                        failure: function(result, request) {

                        }
                    });

                    var fsloadRpi = function() {
                        Ext.Ajax.request({
                            url: Ext.SROOT + 'rpiview/formrpiitems',
                            method: 'GET',
                            success: function(result, request) {
                                var sfields = Ext.util.JSON.decode(result.responseText);
                                formParametro.removeAll();
                                formParametro.add(sfields);
                                formServicio.doLayout();
                            },
                            failure: function(result, request) {

                            }
                        });
                    };

                    fsloadRpi();
                }

            };
            Ext.onReady(domain.Panel.init, domain.Panel);

        </script>    
    </head>
    <body>
    </body>
</html>

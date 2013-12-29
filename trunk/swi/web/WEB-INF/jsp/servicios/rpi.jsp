<%--
    Document   : Configucacion de RPI
    Created on : 30-11-2013, 07:05:10 PM
    Author     : John Castillo Valencia
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<!DOCTYPE HTML>
<html lang="es">
    <head>
        <title>Configucacion de RPI</title>
        <!-- ALL ExtJS Framework resources -->
        <%@include file="../ExtJSScripts-ES.jsp"%>  

        <script type="text/javascript">
            Ext.namespace('domain');
            domain.Manager = {
                mustBeSelect: function() {
                    Ext.MessageBox.show({
                        title: 'Aviso',
                        msg: 'Debe seleccionar los <b>Campos en los servicios</b>.',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.Msg.INFO
                    });
                },
                addField: function(options) {

                    var form = new Ext.FormPanel({
                        url: Ext.SROOT + 'rpi/definirservicio',
                        border: false,
                        title: 'Nuevo Campo',
                        frame: true,
                        bodyStyle: 'padding:10px',
                        labelWidth: 150,
                        autoHeight: true,
                        region: 'center',
                        defaults: {
                            msgTarget: 'side',
                            width: 200
                        },
                        items: [{
                                xtype: 'combo',
                                fieldLabel: 'Tipo de dato',
                                hiddenName: 'tipo',
                                forceSelection: true,
                                allowBlank: false,
                                store: new Ext.data.ArrayStore({
                                    fields: ['type', 'objeto'],
                                    data: [['textfield', 'Cadena'], ['textarea', 'Texto'], ['numberfield', 'Entero'], ['numberfield', 'Real'], ['checkbox', 'Falso/Verdadero'], ['datefield', 'Fecha']]
                                }),
                                valueField: 'type',
                                displayField: 'objeto',
                                typeAhead: true,
                                mode: 'local',
                                triggerAction: 'all',
                                emptyText: 'Selecione el tipo...',
                                selectOnFocus: true
                            }, {
                                xtype: 'checkbox',
                                fieldLabel: 'Requerido',
                                name: 'requerido'
                            }, {
                                xtype: 'textfield',
                                fieldLabel: 'Nombre a Desplegar',
                                allowBlank: false,
                                name: 'etiqueta'
                            }, {
                                xtype: 'textfield',
                                fieldLabel: 'Valor por Defecto',
                                name: 'valordefecto'
                            }, ]
                    });
                    var tree = new Ext.tree.TreePanel({
                        title: 'Servicios disponibles',
                        region: 'east',
                        width: 300,
                        minWidth: 250,
                        maxWidth: 400,
                        useArrows: true,
                        autoScroll: true,
                        animate: true,
                        enableDD: true,
                        containerScroll: true,
                        rootVisible: false,
                        split: true,
                        collapsible: true,
                        root: {
                            nodeType: 'async'
                        },
                        dataUrl: 'treeuserservices',
                        listeners: {
                            'checkchange': function(node, checked) {
                                if (checked) {
                                    node.getUI().addClass('complete');
                                } else {
                                    node.getUI().removeClass('complete');
                                }
                            }
                        }
                    });

                    var win = new Ext.Window({
                        title: 'Agregar campo',
                        autoScroll: true,
                        width: 800,
                        height: 300,
                        maximizable: true,
                        layout: 'border',
                        items: [form, tree],
                        modal: true,
                        buttonAlign: 'center',
                        buttons: [{
                                text: 'Guardar',
                                handler: function() {
                                    var selNodes = tree.getChecked();
                                    var nodessel = '';
                                    Ext.each(selNodes, function(node) {
                                        if (nodessel.length === 0) {
                                            nodessel += node.id;
                                        } else {
                                            nodessel += ":" + node.id;
                                        }
                                    });
                                    if (form.getForm().isValid()) {
                                        if (selNodes.length > 0) {
                                            var fdata = form.getForm().getValues();
                                            fdata.serviceParamsIds = nodessel;
                                            Ext.Ajax.request({
                                                url: Ext.SROOT + 'rpi/guardarcampo',
                                                method: 'POST',
                                                params: fdata,
                                                success: function(result, request) {
                                                    options.handler();
                                                    win.close();
                                                },
                                                failure: function(result, request) {

                                                }
                                            });
                                        } else {
                                            domain.Manager.mustBeSelect();
                                        }
                                    }
                                }
                            }]
                    });
                    win.show();
                },
                deleteField: function(options) {
                    Ext.MessageBox.confirm('Confirmar', '¿Confirma eliminar el registro? Se perderan Datos.', function(r) {
                        if (r === 'yes') {
                            Ext.Ajax.request({
                                url: Ext.SROOT + 'rpi/eliminarcampo',
                                method: 'POST',
                                params: {
                                    id: options.id
                                },
                                success: function(result, request) {
                                    options.handler();
                                },
                                failure: function(result, request) {

                                }
                            });
                        }
                    });
                }
            }

            domain.Panel = {
                init: function() {

                    var formParametro = new Ext.FormPanel({
                        url: 'individual/guardarparametros',
                        border: true,
                        bodyStyle: 'padding:10px',
                        labelWidth: 150,
                        items: []
                    });

                    var formServicio = new Ext.Panel({
                        border: true,
                        //frame: true,
                        region: 'north',
                        height: 300,
                        split: true,
                        items: [/*formParametro*/],
                        tbar: [{
                                text: 'Agregar campo',
                                iconCls: 'create',
                                handler: function() {
                                    domain.Manager.addField({
                                        form: formParametro,
                                        handler: function() {
                                            fsloadRpi();
                                            fsloadRpiAll();
                                        }
                                    })
                                }
                            }]
                    });
                    var rpifields = new Ext.FormPanel({
                        border: true,
                        bodyStyle: 'padding:10px',
                        labelWidth: 130,
                        region: 'center',
                        items: []
                    });

                    var centro = new Ext.Panel({
                        title: 'Servicios',
                        region: 'center',
                        layout: 'border',
                        items: [formServicio, rpifields]
                    });

                    var derecha = new Ext.Panel({
                        title: 'Servicios Disponibles',
                        region: 'east',
                        collapsible: true,
                        split: true,
                        autoScroll: true,
                        width: 400,
                        minWidth: 400,
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

                    //storeServicio.load();
                    var setServiceEnabled = function(e) {
                        Ext.Ajax.request({
                            url: Ext.SROOT + 'rpi/setserviceenabled',
                            method: 'POST',
                            params: {id: e},
                            success: function(result, request) {
                                fsload();
                                fsloadRpiAll();
                            },
                            failure: function(result, request) {

                            }
                        });
                    };

                    var fsload = function() {
                        Ext.Ajax.request({
                            url: Ext.SROOT + 'rpi/formserviceitems',
                            method: 'GET',
                            success: function(result, request) {
                                var sfields = Ext.util.JSON.decode(result.responseText);
                                //console.log(sfields)
                                Ext.each(sfields, function(field, index) {
                                    //console.log(field.buttons[0])
                                    field.buttons[0].handler = function(e) {
                                        setServiceEnabled(field.id);
                                    }
                                });
                                var form = new Ext.FormPanel({
                                    url: Ext.SROOT + 'submitservice',
                                    border: false,
                                    autoHeight: true,
                                    bodyStyle: 'padding:10px',
                                    labelWidth: 90,
                                    frame: false,
                                    items: sfields
                                });
                                derecha.removeAll();
                                derecha.add(form);
                                derecha.doLayout();
                            },
                            failure: function(result, request) {

                            }
                        });
                    };

                    fsload();

                    var fsloadRpi = function() {
                        Ext.Ajax.request({
                            url: Ext.SROOT + 'rpi/formrpiitems',
                            method: 'GET',
                            success: function(result, request) {
                                var sfields = Ext.util.JSON.decode(result.responseText);
                                var cfields = new Array(); //composedfields
                                Ext.each(sfields, function(field, index) {
                                    var nfield = {
                                        xtype: 'compositefield',
                                        fieldLabel: field.etiqueta,
                                        items: [field, {
                                                xtype: 'displayfield',
                                                width: 50,
                                                value: '      '
                                            }, {
                                                xtype: 'button',
                                                iconCls: 'delete',
                                                tooltip: 'eliminar campo',
                                                name: 'ss',
                                                handler: function() {
                                                    domain.Manager.deleteField({
                                                        id: field.id.split(':')[0],
                                                        handler: function() {
                                                            fsloadRpi();
                                                            fsloadRpiAll();
                                                        }
                                                    });
                                                }
                                            }]
                                    };
                                    cfields.push(nfield);
                                });

                                var form = new Ext.FormPanel({
                                    url: Ext.SROOT + 'submitservice',
                                    border: false,
                                    autoHeight: true,
                                    bodyStyle: 'padding:10px',
                                    labelWidth: 130,
                                    frame: false,
                                    //labelAlign: 'top',
                                    items: cfields
                                });
                                formServicio.removeAll();
                                formServicio.add(form);
                                formServicio.doLayout();
                                //fsloadRpiAll();
                            },
                            failure: function(result, request) {

                            }
                        });
                    };

                    fsloadRpi();

                    var moveField = function(name, dir) {
                        Ext.Ajax.request({
                            url: Ext.SROOT + 'rpi/movefield',
                            method: 'POST',
                            params: {name: name, dir: dir},
                            success: function(result, request) {
                                fsloadRpiAll();
                            },
                            failure: function(result, request) {

                            }
                        });
                    };

                    var fsloadRpiAll = function() {
                        Ext.Ajax.request({
                            url: Ext.SROOT + 'rpiview/formrpiitems',
                            method: 'GET',
                            success: function(result, request) {
                                var sfields = Ext.util.JSON.decode(result.responseText);

                                var cfields = new Array(); //composedfields
                                Ext.each(sfields, function(field, index) {
                                    var nfield = {
                                        xtype: 'compositefield',
                                        fieldLabel: field.etiqueta,
                                        items: [field, {
                                                xtype: 'displayfield',
                                                width: 50,
                                                value: '      '
                                            }, {
                                                xtype: 'button',
                                                iconCls: 'arrow-up',
                                                tooltip: 'Subir',
                                                handler: function() {
                                                    moveField(field.name, 'up');
                                                }
                                            }, {
                                                xtype: 'button',
                                                iconCls: 'arrow-down',
                                                tooltip: 'Bajar',
                                                handler: function() {
                                                    moveField(field.name, 'down');
                                                }
                                            }]
                                    };
                                    cfields.push(nfield);
                                });

                                rpifields.removeAll();
                                rpifields.add(cfields);
                                rpifields.doLayout();
                            },
                            failure: function(result, request) {

                            }
                        });
                    };

                    fsloadRpiAll();
                }

            };
            Ext.onReady(domain.Panel.init, domain.Panel);

        </script>    
    </head>
    <body>
    </body>
</html>

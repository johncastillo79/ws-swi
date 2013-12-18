<%--
    Document   : Configucacion de Parametros
    Created on : 28-11-2013, 09:55:10 PM
    Author     : Marcelo Cardenas
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<!DOCTYPE HTML>
<html lang="es">
    <head>
        <title>Configucacion de Parametros</title>
        <!-- ALL ExtJS Framework resources -->
        <%@include file="../ExtJSScripts-ES.jsp"%>  

        <script type="text/javascript">
            Ext.namespace('domain');
            domain.Panel = {
                init: function() {
                    var storeParametros = new Ext.data.JsonStore({
                        url: Ext.SROOT + 'individual/listaparametros',
                        fields: [{name: 'id'},
                            {name: 'servicio_id'},
                            {name: 'nombre'},
                            {name: 'etiqueta'},
                            {name: 'tipo'},
                            {name: 'requerido'},
                            {name: 'valordefecto'},
                            {name: 'oculto'}
                        ]
                    });
                    var gridPanelParametros = new Ext.grid.GridPanel({
                        title: 'Parametros',
                        region: 'center',
                        columns: [
                            {header: "Nombre", width: 100, sortable: true, dataIndex: 'nombre'},
                            {header: "Etiqueta", width: 100, sortable: true, dataIndex: 'etiqueta'},
                            {header: "Tipo", width: 100, sortable: true, dataIndex: 'tipo'},
                            {header: "Requerido", width: 100, sortable: true, dataIndex: 'requerido',
                                renderer: function(val) {
                                    if (val) {
                                        return 'Si'
                                    } else {
                                        return 'No'
                                    }
                                }},
                            {header: "Valor por Defecto", width: 100, sortable: true, dataIndex: 'valordefecto'},
                            {header: "Oculto", width: 100, sortable: true, dataIndex: 'oculto',
                                renderer: function(val) {
                                    if (val) {
                                        return 'Si'
                                    } else {
                                        return 'No'
                                    }
                                }}
                        ],
                        store: storeParametros
                    });

                    gridPanelParametros.getSelectionModel().on('rowselect', function rowselected(sm, rowindex, record) {
                        record.data['servicio.id'] = cboBusServicio.getValue();
                        formParametro.getForm().loadRecord(record);
                    });

                    function fun_buscar() {
                        storeParametros.load({params: {servicio_id: cboBusServicio.getValue()}});
                    }
                    ;

                    var storeServicio = new Ext.data.JsonStore({
                        url: Ext.SROOT + 'individual/listaservicios',
                        fields: [{name: 'id'},
                            {name: 'nombre'}
                        ]
                    });

                    var cboBusServicio = new Ext.form.ComboBox({
                        fieldLabel: 'Servicio',
                        name: 'cboBusServicio',
                        forceSelection: true,
                        store: storeServicio,
                        //emptyText:'servicio..',
                        triggerAction: 'all',
                        lastQuery: '', //hideTrigger:true,
                        editable: false,
                        displayField: 'nombre',
                        valueField: 'id',
                        typeAhead: true,
                        selectOnFocus: true
                    });

                    var formServicio = new Ext.FormPanel({
                        border: false,
                        region: 'north',
                        height: 150,
                        defaults: {xtype: 'textfield'},
                        bodyStyle: 'padding:10px',
                        items: [
                            new Ext.form.FieldSet({
                                title: 'Seleccionar Servicio',
                                autoHeight: true,
                                defaultType: 'textfield',
                                items: [cboBusServicio],
                                buttons: [{
                                        text: 'Cargar',
                                        handler: function() {
                                            fun_buscar();
                                        }}
                                ]
                            })
                        ]
                    });

                    var formParametro = new Ext.FormPanel({
                        url: 'individual/guardarparametros',
                        border: false,
                        bodyStyle: 'padding:10px',
                        labelWidth: 150,
                        frame: true,
                        items: [
                            new Ext.form.FieldSet({
                                title: 'Propiedades del parametro',
                                autoHeight: true,
                                defaults: {width: 200},
                                items: [{
                                        xtype: 'textfield',
                                        fieldLabel: 'Nombre Parametro',
                                        readOnly: true,
                                        name: 'nombre'
                                    }, {
                                        xtype: 'combo',
                                        fieldLabel: 'Tipo de dato',
                                        hiddenName: 'tipo',
                                        forceSelection: true,
                                        store: new Ext.data.ArrayStore({
                                            fields: ['type', 'objeto'],
                                            data: [['string', 'Cadena'], ['text', 'Texto'], ['int', 'Entero'], ['float', 'Real'], ['truefalse', 'Falso/Verdadero'], ['date', 'Fecha']]
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
                                        xtype: 'checkbox',
                                        fieldLabel: 'Oculto',
                                        name: 'oculto'
                                    }, {
                                        xtype: 'textfield',
                                        fieldLabel: 'Nombre a Desplegar',
                                        allowBlank: false,
                                        name: 'etiqueta'
                                    }, {
                                        xtype: 'textfield',
                                        fieldLabel: 'Valor por Defecto',
                                        name: 'valordefecto'
                                    }, {
                                        xtype: 'hidden',
                                        name: 'id'
                                    }, {
                                        xtype: 'hidden',
                                        name: 'servicio.id'
                                    }]
                            })
                        ],
                        //buttonAlign: 'center',
                        buttons: [
                            {text: 'Guardar', handler: function() {
                                    Ext.MessageBox.confirm('Confirmar', '¿Confirma guardar los cambios?', function(r) {
                                        if (r === 'yes') {
                                            formParametro.getForm().submit({
                                                success: function(form, action) {
                                                    gridPanelParametros.store.reload();
                                                },
                                                failure: function(form, action) {
                                                    if (action.Failure === 'server') {
                                                        var r = Ext.util.JSON.decode(action.response.responseText);
                                                        alert(r.errorMessage);
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }},
                            {text: 'Limpiar', handler: function() {
                                    formParametro.getForm().reset();
                                }}
                        ]
                    });

                    var centro = new Ext.Panel({
                        title: 'Servicios',
                        region: 'center',
                        layout: 'border',
                        items: [formServicio, gridPanelParametros]
                    });

                    var derecha = new Ext.Panel({
                        title: 'Configuración de parámetros del servicio',
                        region: 'east',
                        collapsible: true,
                        split: true,
                        autoScroll: true,
                        width: 550,
                        minWidth: 500,
                        layout: 'anchor',
                        items: [formParametro]
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

                    storeServicio.load();
                }
            };
            Ext.onReady(domain.Panel.init, domain.Panel);

        </script>    
    </head>
    <body>
    </body>
</html>

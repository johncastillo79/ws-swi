
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

            com.icg.FileUpload = {
                init: function() {
                    Ext.QuickTips.init();
                    var sm = null;
                    var gridcfg = null;
                    //var data = [{"/return/nombres": "Oscar1", "/return/sexo": "M", "/ciudad/idCiudad": "1", "_root_": "1", "/telefono/id_telefono": "3", "/return/edad": "31", "/return/id_persona": "1", "/ciudad/nomCiudad": "Ciudad 1", "/telefono/tefono": "712-230102"}, {"/return/nombres": "Oscar1", "/return/sexo": "M", "/ciudad/idCiudad": "2", "_root_": "1", "/telefono/id_telefono": "3", "/return/edad": "31", "/return/id_persona": "1", "/ciudad/nomCiudad": "Ciudad 2", "/telefono/tefono": "712-230102"}, {"/return/nombres": "Oscar1", "/return/sexo": "M", "/ciudad/idCiudad": "3", "_root_": "1", "/telefono/id_telefono": "3", "/return/edad": "31", "/return/id_persona": "1", "/ciudad/nomCiudad": "Ciudad 3", "/telefono/tefono": "712-230102"}, {"/return/nombres": "Oscar1", "/return/sexo": "M", "/ciudad/idCiudad": "4", "_root_": "1", "/telefono/id_telefono": "3", "/return/edad": "31", "/return/id_persona": "1", "/ciudad/nomCiudad": "Ciudad 4", "/telefono/tefono": "712-230102"}, {"/return/nombres": "Oscar1", "/return/sexo": "M", "/ciudad/idCiudad": "5", "_root_": "1", "/telefono/id_telefono": "3", "/return/edad": "31", "/return/id_persona": "1", "/ciudad/nomCiudad": "Ciudad 5", "/telefono/tefono": "712-230102"}, {"/return/nombres": "Oscar1", "/return/sexo": "M", "/ciudad/idCiudad": "6", "_root_": "1", "/telefono/id_telefono": "3", "/return/edad": "31", "/return/id_persona": "1", "/ciudad/nomCiudad": "Ciudad 6", "/telefono/tefono": "712-230102"}];
                    var data = [{"telefonos/id":"0","//id":"1","telefonos/numero":"70685900","ciudad/idCiudad":"0","ciudad/nombre":"ciudad 0","//nombre":"John"},{"telefonos/id":"0","//id":"1","telefonos/numero":"70685900","ciudad/idCiudad":"1","ciudad/nombre":"ciudad 1","//nombre":"John"},{"telefonos/id":"1","//id":"1","telefonos/numero":"70685901","ciudad/idCiudad":"0","ciudad/nombre":"ciudad 0","//nombre":"John"},{"telefonos/id":"1","//id":"1","telefonos/numero":"70685901","ciudad/idCiudad":"1","ciudad/nombre":"ciudad 1","//nombre":"John"},{"telefonos/id":"2","//id":"1","telefonos/numero":"70685902","ciudad/idCiudad":"0","ciudad/nombre":"ciudad 0","//nombre":"John"},{"telefonos/id":"2","//id":"1","telefonos/numero":"70685902","ciudad/idCiudad":"1","ciudad/nombre":"ciudad 1","//nombre":"John"},{"telefonos/id":"0","//id":"2","telefonos/numero":"70685900","ciudad/idCiudad":"0","ciudad/nombre":"ciudad 0","//nombre":"Oscar"},{"telefonos/id":"0","//id":"2","telefonos/numero":"70685900","ciudad/idCiudad":"0","ciudad/nombre":"ciudad 0","//nombre":"Oscar"},{"telefonos/id":"1","//id":"2","telefonos/numero":"70685901","ciudad/idCiudad":"1","ciudad/nombre":"ciudad 1","//nombre":"Oscar"},{"telefonos/id":"1","//id":"2","telefonos/numero":"70685901","ciudad/idCiudad":"1","ciudad/nombre":"ciudad 1","//nombre":"Oscar"},{"telefonos/id":"2","//id":"2","telefonos/numero":"70685902","ciudad/idCiudad":"2","ciudad/nombre":"ciudad 2","//nombre":"Oscar"},{"telefonos/id":"2","//id":"2","telefonos/numero":"70685902","ciudad/idCiudad":"2","ciudad/nombre":"ciudad 2","//nombre":"Oscar"}];
                    var cols = new Array();
                    if (sm) {
                        cols.push(sm);
                    } else if (data.length > 5) {
                        cols.push(new Ext.grid.RowNumberer({
                            width: 29
                        }));
                    }
                    var isObject = function(val) {
                        if(val instanceof Object) {                            
                            console.log(val);
                            return 'Mas..'
                        } else {
                            return val;
                        }
                    };
                    
                    var fields = new Array();
                    for (var prop in data[0]) {
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
                            data: data,
                            autoLoad: true
                        }),
                        renderTo:'grid',        
                        title: 'Recs',
                        height: 500,
                        columns: cols
                    };
                    if (sm) {
                        gridcfg.sm = sm;
                    }
                    var grid = new Ext.grid.GridPanel(gridcfg);

                }
            }
            Ext.onReady(com.icg.FileUpload.init, com.icg.FileUpload);
        </script>

    </head>
    <body>
        <div id="form"></div>
        <div id="grid"></div>
    </body>
</html>


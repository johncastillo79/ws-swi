
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

                    // create the Data Store
                    var store = new Ext.data.Store({
                        // load using HTTP
                        url: 'soap_response.xml',
                        // the return will be XML, so lets set up a reader
                        reader: new Ext.data.XmlReader({
                            // records will have an "Item" tag
                            record: 'Artist',
                            //id: 'ASIN',
                            totalRecords: '@total'
                        }, [
                            // set up the fields mapping into the xml doc
                            // The first needs mapping, the others are very basic
                            //{name: 'Name', mapping: 'ItemAttributes > Author'},
                            //'Title', 'Manufacturer', 'ProductGroup'
                            {name: 'Name', mapping: 'Name'},
                        ])
                    });

                    // create the grid
                    var grid = new Ext.grid.GridPanel({
                        store: store,
                        columns: [
                            {header: "Author", width: 120, dataIndex: 'Name', sortable: true},
                            {header: "Title", width: 180, dataIndex: '', sortable: true},
                            {header: "Manufacturer", width: 115, dataIndex: '', sortable: true},
                            {header: "Product Group", width: 100, dataIndex: '', sortable: true}
                        ],
                        renderTo: 'grid',
                        width: 540,
                        height: 200
                    });

                    store.load();

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


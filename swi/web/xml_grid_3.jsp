
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
        <link rel="stylesheet" type="text/css" href="/ext-3.3.1/examples/ux/treegrid/treegrid.css" rel="stylesheet" />
        <script type="text/javascript" src="/ext-3.3.1/adapter/ext/ext-base.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/ext-all.js"></script>    
        <link rel="stylesheet" type="text/css" href="css/icons.css"/>

        <script type="text/javascript" src="/ext-3.3.1/examples/ux/treegrid/TreeGridSorter.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/examples/ux/treegrid/TreeGridColumnResizer.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/examples/ux/treegrid/TreeGridNodeUI.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/examples/ux/treegrid/TreeGridLoader.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/examples/ux/treegrid/TreeGridColumns.js"></script>
        <script type="text/javascript" src="/ext-3.3.1/examples/ux/treegrid/TreeGrid.js"></script>


        <script type="text/javascript">
            Ext.BLANK_IMAGE_URL = '/ext-3.3.1/resources/images/default/s.gif';
            Ext.ns('com.icg');

            com.icg.FileUpload = {
                init: function() {
                    Ext.QuickTips.init();

                    var tree = new Ext.tree.TreePanel({
                        title: 'Core Team Projects',
                        width: 500,
                        height: 300,
                        //renderTo: Ext.getBody(),
                        enableDD: true,                        
                        loader: new Ext.tree.TreeLoader(),
                    });

                    var json = {"name": "ListAllArtistsResult", "leaf": false, "children": [{"name": "Artist", "leaf": false, "attributes": {"Name": "Mr Guy"}, "children": [{"name": "Albums", "leaf": false, "children": [{"name": "Album", "leaf": false, "attributes": {"Name": "hi"}, "children": [{"name": "SongNames", "leaf": false, "children": [{"name": "string", "leaf": true, "attributes": {"string": "foo"}}, {"name": "string", "leaf": true, "attributes": {"string": "bar"}}, {"name": "string", "leaf": true, "attributes": {"string": "baz"}}]}]}]}]}, {"name": "Artist", "leaf": false, "attributes": {"Name": "Mr Buddy"}, "children": [{"name": "Albums", "leaf": false, "children": [{"name": "Album", "leaf": false, "attributes": {"Name": "salut"}, "children": [{"name": "SongNames", "leaf": false, "children": [{"name": "string", "leaf": true, "attributes": {"string": "green"}}, {"name": "string", "leaf": true, "attributes": {"string": "orange"}}, {"name": "string", "leaf": true, "attributes": {"string": "red"}}]}]}]}]}, {"name": "Artist", "leaf": false, "attributes": {"Name": "Mr Friend"}, "children": [{"name": "Albums", "leaf": false, "children": [{"name": "Album", "leaf": false, "attributes": {"Name": "hey"}, "children": [{"name": "SongNames", "leaf": false, "children": [{"name": "string", "leaf": true, "attributes": {"string": "brown"}}, {"name": "string", "leaf": true, "attributes": {"string": "pink"}}, {"name": "string", "leaf": true, "attributes": {"string": "blue"}}]}]}, {"name": "Album", "leaf": false, "attributes": {"Name": "hello"}, "children": [{"name": "SongNames", "leaf": false, "children": [{"name": "string", "leaf": true, "attributes": {"string": "apple"}}, {"name": "string", "leaf": true, "attributes": {"string": "orange"}}, {"name": "string", "leaf": true, "attributes": {"string": "pear"}}]}]}]}]}]}
                    // set the root node
                    var root = new Ext.tree.AsyncTreeNode({
                        text: 'Autos',
                        draggable: false,
                        id: 'source',
                        children: json
                    });

                    tree.setRootNode(root);

                    //tree.render();
                    root.expand();
                }
            }
            Ext.onReady(com.icg.FileUpload.init, com.icg.FileUpload);
        </script>
    </head>
    <body></body>
</html>


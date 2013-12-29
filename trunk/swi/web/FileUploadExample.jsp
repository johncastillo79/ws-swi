<%-- 
    Document   : John
    Created on : 26-04-2011, 02:30:51 PM
    Author     : John
--%>

<html>
    <head>
        <title>File Upload Example</title>
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
                init: function(){
                    Ext.QuickTips.init();
 
                    var fp = new Ext.FormPanel({
                        renderTo: 'form',
                        fileUpload: true,
                        width: 500,
                        frame: true,
                        title: 'File Upload Form',
                        autoHeight: true,
                        bodyStyle: 'padding: 10px 10px 0 10px;',
                        labelWidth: 50,
                        defaults: {
                            anchor: '95%',
                            allowBlank: false,
                            msgTarget: 'side'
                        },
                        items: [{
                                xtype: 'textfield',
                                fieldLabel: 'Name',
                                name:'name'
                            },{
                                xtype: 'fileuploadfield',
                                id: 'form-file',
                                emptyText: 'Select an image',
                                fieldLabel: 'Photo',
                                name: 'fileData',
                                buttonText: '',
                                buttonCfg: {
                                    iconCls: 'upload-icon'
                                }
                            }],
                        buttons: [{
                                text: 'Save',
                                handler: function(){
                                    if(fp.getForm().isValid()){
                                        fp.getForm().submit({
                                            url: 'upload/file',
                                            waitMsg: 'Uploading your photo...',
                                            success: function(fp, o){
                                                msg('Success', 'Processed file "'+o.result.file+'" on the server');
                                            }
                                        });
                                    }
                                }
                            },{
                                text: 'Reset',
                                handler: function(){
                                    fp.getForm().reset();
                                }
                            }]
                    });    
 
 
 
 
 
 
 
 
 
 
 
 
 
 
                    //                    var msg = function(title, msg){
                    //                        Ext.Msg.show({
                    //                            title: title,
                    //                            msg: msg,
                    //                            minWidth: 200,
                    //                            modal: true,
                    //                            icon: Ext.Msg.INFO,
                    //                            buttons: Ext.Msg.OK
                    //                        });
                    //                    };
                    // 
                    //                    var form = new Ext.FormPanel({
                    //                        renderTo: 'form',
                    //                        url: 'upload/file',
                    //                        fileUpload: true,
                    //                        width: 500,
                    //                        frame: true,
                    //                        title: 'File Upload Form',
                    //                        autoHeight: true,
                    //                        bodyStyle: 'padding: 10px 10px 0 10px;',
                    //                        labelWidth: 50,
                    //                        defaults: {
                    //                            anchor: '95%',
                    //                            allowBlank: false,
                    //                            msgTarget: 'under'
                    //                        },
                    //                        items: [{
                    //                                xtype: 'fileuploadfield',                                
                    //                                emptyText: 'Seleccione una foto para subir',
                    //                                fieldLabel: 'Foto',
                    //                                name: 'fileData',
                    //                                buttonCfg: {
                    //                                    text: '',
                    //                                    iconCls: 'upload-icon'
                    //                                }
                    //                            }],
                    //                        buttons: [{
                    //                                text: 'Upload',
                    //                                handler: function(){
                    //                                    //if(form.getForm().isValid()){
                    //                                        
                    //                                        form.getForm().submit({                                            
                    ////                                            waitMsg: 'Enviando su foto...',
                    ////                                            success:function(form, action){
                    ////                                                msg('Success', 'Processed file on the server');
                    ////                                                form.getForm().reset();
                    ////                                            },
                    ////                                            failure:function(form, action) {
                    ////                                                var rp = Ext.util.JSON.decode(action.response.responseText);                                
                    ////                                                Ext.MessageBox.show({
                    ////                                                    title:'Error',
                    ////                                                    msg:rp.errorMessage,
                    ////                                                    buttons: Ext.MessageBox.OK,
                    ////                                                    icon:Ext.Msg.ERROR
                    ////                                                });
                    ////                                            }
                    //                                        });
                    //                                    //}
                    //                                }
                    //                            },{
                    //                                text: 'Reset',
                    //                                handler: function(){
                    //                                    form.getForm().reset();
                    //                                }
                    //                            }]
                    //                    });
                }     
            }
            Ext.onReady(com.icg.FileUpload.init,com.icg.FileUpload);
        </script>

    </head>
    <body>
        <div id="form"></div>
        <div id="grid"></div>
    </body>
</html>

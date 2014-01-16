/**
 * IframePanel Class
 * @Description Component Admin
 * @author Johns Castillo Valencia
 */
//Iframe Panel version 1.0
//Ext.IframePanel = Ext.extend(Ext.Panel, {
//    onRender: function() {
//        this.bodyCfg = {
//            tag: 'iframe',
//            src: this.src,
//            cls: this.bodyCls,
//            style: {
//                border: '0px none'
//            }
//        };
//        Ext.IframePanel.superclass.onRender.apply(this, arguments);
//    }
//});
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





/**
 * openModule, Open Module in new TAB 
 * @augments, 
 *   id:
 *   icono:
 *   title: 
 *   url: url direct to program 
 * @author Johns Castillo Valencia
 */
swi.ui.openModule = function(options) {
    if (options.fn) {
        var ret = eval(options.fn);
    } else {
        var tab = swi.ui.main.tabs.findById(options.id);
        if (!tab) {
            var siframe = new Ext.IframePanel({
                src: options.url ? options.url : '404.jsp'
            });
            var tabCfg = {
                id: options.id,
                iconCls: options.iconCls ? options.iconCls : 'module-app',
                layout: 'fit',
                closable: true,
                title: options.title ? options.title : 'Sin titulo',
                items: siframe
            };
            if (options.tbar) {
                tabCfg.tbar = options.tbar;
            }

            tab = new Ext.Panel(tabCfg);
            swi.ui.main.tabs.add(tab);
        }
        swi.ui.main.tabs.activate(tab);
    }
};

swi.ui.main = {
    start: function() {
        var start = new Ext.Panel({
            id: 'start-panel',
            title: "Servicios",
            layout: 'fit',
            autoScroll: true,
            bodyStyle: 'padding:25px',
            contentEl: 'start-div'
        });
        this.tabs = new Ext.TabPanel({
            border: false,
            activeTab: 0,
            enableTabScroll: true,
            items: [start],
            plugins: new Ext.ux.TabCloseMenu()
        });
        return {
            id: 'content-panel',
            region: 'center',
            activeItem: 0,
            layout: 'fit',
            tbar: Ext.appMenu(),
            items: [this.tabs]
        };
    },
    init: function() {
        Ext.History.init();
        new Ext.Viewport({
            layout: 'border',
            items: [{
                    xtype: 'box',
                    region: 'north',
                    frame: true,
                    applyTo: 'header',
                    height: 50
                }, swi.ui.main.start(), {
                    xtype: 'box',
                    region: 'south',
                    frame: true,
                    applyTo: 'footer',
                    height: 40
                }]
        });
    }
};

Ext.onReady(swi.ui.main.init);
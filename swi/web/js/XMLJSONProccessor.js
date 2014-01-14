/**
 Item Name : XML JSON to GRID   
 Author : John Castillo Valencia
 Contact: john.gnu@gmail.com
 Version : 1.1
 */
var data = {
    "name": "ListAllArtistsResult",
    "level": 0,
    "children": [{
            "name": "Artist",
            "level": 0,
            "attributes": {
                "Name": "Mr Guy"
            },
            "children": [{
                    "name": "Albums",
                    "level": 0,
                    "children": [{
                            "name": "Album",
                            "level": 0,
                            "attributes": {
                                "Name": "hi"
                            },
                            "children": [{
                                    "name": "SongNames",
                                    "level": 0,
                                    "children": [{
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "foo"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "bar"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "baz"
                                            }
                                        }]
                                }]
                        }]
                }]
        }, {
            "name": "Artist",
            "level": 0,
            "attributes": {
                "Name": "Mr Buddy"
            },
            "children": [{
                    "name": "Albums",
                    "level": 0,
                    "children": [{
                            "name": "Album",
                            "level": 0,
                            "attributes": {
                                "Name": "salut"
                            },
                            "children": [{
                                    "name": "SongNames",
                                    "level": 0,
                                    "children": [{
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "green"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "orange"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "red"
                                            }
                                        }]
                                }]
                        }]
                }]
        }, {
            "name": "Artist",
            "level": 0,
            "attributes": {
                "Name": "Mr Friend"
            },
            "children": [{
                    "name": "Albums",
                    "level": 0,
                    "children": [{
                            "name": "Album",
                            "level": 0,
                            "attributes": {
                                "Name": "hey"
                            },
                            "children": [{
                                    "name": "SongNames",
                                    "level": 0,
                                    "children": [{
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "brown"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "pink"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "blue"
                                            }
                                        }]
                                }]
                        }, {
                            "name": "Album",
                            "level": 0,
                            "attributes": {
                                "Name": "hello"
                            },
                            "children": [{
                                    "name": "SongNames",
                                    "level": 0,
                                    "children": [{
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "apple"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "orange"
                                            }
                                        }, {
                                            "name": "string",
                                            "level": 0,
                                            "attributes": {
                                                "string": "pear"
                                            }
                                        }]
                                }]
                        }]
                }]
        }]
};


var str = '[{';
function setter(node) {
    if (node.parent) {
        for (var prop in node.attributes) {
            str = str + '"' + node.name + '/' + prop + '":"' + node.attributes[prop] + (node.parent.parent ? '",' : '"');
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
        str = str + '},{'
    }
}
//USE
data.parent = null;
decoderTree(data);
str = str.substring(0, str.length - 2) + ']';
console.log(str);




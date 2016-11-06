var fs = require("fs");
var file = JSON.parse(fs.readFileSync("./data.json", "utf8"));

// use strict;

var st = function() {
  //var s = file.filter(function(c){ return c["oos:code"].slice(0,2) ===  });
  //fs.writeFileSync('./code.json', JSON.stringify(s) , 'utf-8');
function flatten(array, mutable) {
    var toString = Object.prototype.toString;
    var arrayTypeStr = '[object Array]';

    var result = [];
    var nodes = (mutable && array) || array.slice();
    var node;

    if (!array.length) {
        return result;
    }

    node = nodes.pop();

    do {
        if (toString.call(node) === arrayTypeStr) {
            nodes.push.apply(nodes, node);
        } else {
            result.push(node);
        }
    } while (nodes.length && (node = nodes.pop()) !== undefined);

    result.reverse(); // we reverse result to restore the original order
    return result;
}


  var a = file.map(function(d) {
  	if (d["oos:code"].length === 4) {
	   	var rootSection =  d["oos:code"]; // 01.1
	  	return {
	  		okveds: [{ code: d["oos:code"], title: d["oos:name"]  }],
	  		childs: file.map(function(dd) { // 01.11
	  			if (dd["oos:parentCode"] === d["oos:code"] && dd["oos:code"].length > 4) {

	  			var dd_childs = file.filter(c => (c["oos:parentCode"] == dd["oos:code"] && c["oos:code"] !== dd["oos:code"] ) );
	  			var dd_childs2 = dd_childs.filter(c => c["oos:code"].length > 5).map(function(ddd) { 
	  				return file.filter(c => (c["oos:parentCode"] == ddd["oos:code"] && c["oos:code"] !== ddd["oos:code"] ) ).map(function(q) {
	  					return q["oos:code"];
				})
	  			})

	  			return {
	  				    code: dd["oos:code"], 
	  				    title: dd["oos:name"],
	  				    parent: dd["oos:parentCode"], 
	  				    childs_ids: [dd["oos:code"]].concat(dd_childs.map(z => z["oos:code"])).concat(flatten(dd_childs2)).filter(c => c.length > 5),
	  				    childs: dd_childs.filter(c => c["oos:code"].length > 5).map(function(ddd) {
	  				    		return {
	  				    		parent: ddd["oos:parentCode"], code: ddd["oos:code"], title: ddd["oos:name"],
	  				    		childs:  file.filter(c => (c["oos:parentCode"] == ddd["oos:code"] && c["oos:code"] !== ddd["oos:code"] ) ).map(function(q) {
	  				    			return {code: q["oos:code"],
	  				    			parent: q["oos:parentCode"],
	  				    			title: q["oos:name"]}
	  				    		})  } })
	  				    //[{
	  				    	//code: dd["oos:code"],
	  				    	//title: dd["oos:name"],
//	  				    	childs: dd_childs.filter(c => c["oos:code"].length > 5).map(function(ddd) {
//	  				    		return {
//	  				    		parent: ddd["oos:parentCode"], code: ddd["oos:code"], title: ddd["oos:name"] } })

//	  				    }]
	  		           }; 
	  			}
	  		}).filter(c => c !== undefined),
	  		hidden: false
	  	}
  	}

  }).filter(a => a !== undefined && a !== null );
  //console.log(a.filter(c => { return c != undefined }).forEach(c => console.log(c.childs[0] )) );

  return a;
}


fs.writeFileSync('./code.json', JSON.stringify(st().filter(c => c !== null)) , 'utf-8');

/*
[
{
	okveds: [{code: "01.1", title:"Выращивание однолетних культур" }], 
    childs: [
       {parent: "01.1", code: "01.11", title: "01.11 код", 
         child_ids: ["01.11.1","01.11.11"],

         childs: [{parent: "01.11", code: "01.11.1", title: "01.11.1", 
            childs: [     {parent: "01.11.1", code: "01.11.11", title: "01.11.11"},
                          {parent: "01.11.1", code: "01.11.12", title: "01.11.12"} 
                  ] },

            ], },


       {parent: "01.1", code: "01.12", title: "01.12 код", 
         child_ids: ["01.12.1","01.12.11"],

         childs: [{parent: "01.12", code: "01.12.1", title: "01.12.1", 
            childs: [     {parent: "01.12.1", code: "01.12.11", title: "01.12.11"}      ] },

            ], },



            ], 
hidden: false}
]


*/
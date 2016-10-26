//our root app component
import {Component, NgModule} from '@angular/core'
import {BrowserModule} from '@angular/platform-browser'

@Component({
  selector: 'my-app',
  templateUrl: './assets/app.html' 
})
export class App {
  name:string;
  constructor() {
    this.name = 'Angular2'
    this.selectedOkved = [];
    this.selectedParent = [];


	 this.class = {okveds: [{code: "01.1", title:"Выращивание однолетних культур" }], hidden: false};   


this.allOkveds = [
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
hidden: false},

{
	okveds: [{code: "01.2", title:"Выращивание однолетних культур" }], 
    childs: [
       {parent: "01.2", code: "01.21", title: "01.21 код", 
                child_ids: ["01.21.1","01.21.11"],

         childs: [{parent: "01.21", code: "01.21.1", title: "01.21.1", 
            childs: [     {parent: "01.21.1", code: "01.21.11", title: "01.21.11"}      ] }], }], 
hidden: false},

{
	okveds: [{code: "01.3", title:"Выращивание однолетних культур" }], 
    childs: [
       {parent: "01.3", code: "01.31", title: "01.31 код", 
                child_ids: ["01.31.1","01.31.11"],

         childs: [{parent: "01.31", code: "01.31.1", title: "01.31.1", 
            childs: [     {parent: "01.31.1", code: "01.31.11", title: "01.31.11"}      ] }], }], 
hidden: false},

{
	okveds: [{code: "01.4", title:"Выращивание однолетних культур" }], 
    childs: [
       {parent: "01.4", code: "01.31", title: "01.41 код", 
                child_ids: ["01.41.1","01.41.11"],

         childs: [{parent: "01.41", code: "01.41.1", title: "01.41.1", 
            childs: [     {parent: "01.41.1", code: "01.41.11", title: "01.41.11"}      ] }], }], 
hidden: false},

];


this.isRootNode = function(code) {
  return this.allOkveds.find(function(c) { c.okveds[0].code === code }) !== undefined
}

	 this.class2 = {okveds: [{code: "01.2", title:"Тестовый оквэд 01.2" }], hidden: true};   
	 this.class3 = {okveds: [{code: "01.3", title:"Тестовый оквэд 01.3" }], hidden: true};   
	 this.class4 = {okveds: [{code: "01.4", title:"Тестовый оквэд 01.4" }], hidden: true};   
	 this.class5 = {okveds: [{code: "01.5", title:"Тестовый оквэд 01.5" }], hidden: true};   
	 this.class6 = {okveds: [{code: "01.6", title:"Тестовый оквэд 01.6" }], hidden: true};   
	 this.class7 = {okveds: [{code: "01.7", title:"Тестовый оквэд 01.7" }], hidden: true};   




this.isExistedCode = function(code) {
	return (this.selectedOkved.find(function(d){return d.code === code}) !== undefined)
}
this.isNotExistedParent = function(code) {
	return (this.selectedOkved.find(function(d){return d.parent === code}) == undefined)
}


// код отсутствует для этого 

this.isParent = function(code, parent) {
	return (this.selectedOkved.find(function(d){return d.code === code && d.parent !== parent }) !== undefined)
}
this.getParentObject = function(code) {
	return (this.selectedOkved.find(function(d){return d.code === code }) !== undefined)
}


this.addToSelected = function(okved) {
	console.log('this.addToSelected = function(okved) {', okved);
	if (!this.isExistedCode(okved.code) ) {
		this.selectedOkved.push(okved);
	}
}

// 11.1

// 11.11 *
// 11.22  isParent
/*


     var findGrandFather = selectedOkved.find(function(d){
		var l = selectedOkved.find(function(a){return a.code !== okved.parent});
		var lo;
		if (l !== undefined) {
			return true;			
		} else {
			return false;
		}
     }) !== undefined

*/



this.addChildToSelected = function(okvedParent, okved) {
	console.log('this.addChildToSelected = function(okvedParent) {', okved);
	//var parentNotExist = this.isParent(okved.parent, okved.code)
	var existedParent, currentParent;
    if (okved.parent !== undefined) {
       existedParent = this.isNotExistedParent(okved.code)
       currentParent = !this.isExistedCode(okved.parent)
    } else {
       existedParent = true
    }
    var selectedOkved = this.selectedOkved


    var hasChilds;
    if (okved.child_ids !== undefined && okved.child_ids.length > 0) {
    	hasChilds = okved.child_ids.map(c => this.isExistedCode(c) ).reduce(function(c,a) { return c || a });
    } else {
    	hasChilds = false;
    }
    
    /*
      a]  --------
        b]
          c] *


      a]  *
        b]
          c] -----------
    */

	console.log('existedParent', existedParent, !currentParent, hasChilds)





	if (!this.isExistedCode(okved.code) && existedParent && currentParent && !hasChilds ) {

        if (okved.code.length == 8) {
        	var ccode = okved.code.split(".");
	 		!this.isExistedCode(ccode[0]+"."+ccode[1]) ? this.selectedOkved.push(okved) : false;

        } else {
			this.selectedOkved.push(okved);
        }
	}
}




this.isOkvedSelected = function(okved) {
	return this.selectedOkved.filter(function(el) {
	  return el.code == okved.code;
	}).length > 0
}

this.primaryOkved = null;

this.isPrimaryOkved = function(okved) {
	if (this.primaryOkved !== null && this.primaryOkved.code == okved.code) {
	 return true;
	} else {
	  return false;
	}
}

this.asPrimaryOkved = function(okved) {
	if (this.primaryOkved == null || this.primaryOkved.code !== okved.code) {
	  this.primaryOkved = okved;
	} else {
	  this.primaryOkved = null;
	}
	console.log('primaryOkved',this.primaryOkved)
}








this.removeFromSelected = function(okved) {
	var getParentObject = this.getParentObject;
	this.selectedOkved = this.selectedOkved.filter(function(el) {
	    return el.code !== okved.code;
	});
}


  }
}

@NgModule({
  imports: [ BrowserModule ],
  declarations: [ App ],
  bootstrap: [ App ]
})
export class AppModule {
	
}
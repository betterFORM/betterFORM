/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xfEditorUtil");

/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

var documentChilds = [
            "model",
            "group",
            "repeat",
            "switch"];
var modelChilds = [
            "instance",
            "schema",
            "submission",
            "bind",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var instanceChilds = ["instance-root"];
var instancerootChilds = ["instance-data"];
var instancedataChilds = ["instance-data"];
var bindChilds = [
            "bind"];
var extensionChilds = [];
var labelChilds = [
            "output"];
var hintChilds = [
            "output"];
var helpChilds = [
            "output"];
var alertChilds = [
            "output"];
var itemChilds = [
            "label",
            "value",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var itemsetChilds = [
            "label",
            "value",
            "copy",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var choicesChilds = [
            "label",
            "choices",
            "item",
            "itemset"];
var valueChilds = [];
var copyChilds = [];
var inputChilds = [
            "label",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var secretChilds = [
            "label",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var textareaChilds = [
            "label",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var uploadChilds = [
            "label",
            "filename",
            "mediatype",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var filenameChilds = [
            "filename"];
var mediatypeChilds = [
            "mediatype"];
var select1Childs = [
            "label",
            "item",
            "itemset",
            "choices",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var selectChilds = [
            "label",
            "item",
            "itemset",
            "choices",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var rangeChilds = [
            "label",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var triggerChilds = [
            "label",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var outputChilds = [
            "label",
            "mediatype",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var submitChilds = [
            "label",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var repeatChilds = [
            "input",
            "secret",
            "textarea",
            "output",
            "upload",
            "select1",
            "select",
            "range",
            "submit",
            "trigger",
            "group",
            "switch",
            "repeat",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var groupChilds = [
            "label",
            "input",
            "secret",
            "textarea",
            "output",
            "upload",
            "select1",
            "select",
            "range",
            "submit",
            "trigger",
            "group",
            "switch",
            "repeat",
            "help",
            "hint",
            "alert",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var switchChilds = [
            "label",
            "input",
            "secret",
            "textarea",
            "output",
            "upload",
            "select1",
            "select",
            "range",
            "submit",
            "trigger",
            "group",
            "switch",
            "repeat",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var caseChilds = [
            "label",
            "input",
            "secret",
            "textarea",
            "output",
            "upload",
            "select1",
            "select",
            "range",
            "submit",
            "trigger",
            "group",
            "switch",
            "repeat",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var actionChilds = [
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var setvalueChilds = [];
var insertChilds = [];
var deleteChilds = [];
var setindexChilds = [];
var toggleChilds = [
            "case"];
var setfocusChilds = [
            "control"];
var controlChilds = [
            "control"];
var dispatchChilds = [
            "name",
            "targetid",
            "target",
            "delay"];
var nameChilds = [
            "name"];
var targetidChilds = [
            "targetid"];
var targetChilds = [
            "target"];
var delayChilds = [
            "delay"];
var rebuildChilds = [];
var recalculateChilds = [];
var revalidateChilds = [];
var refreshChilds = [];
var resetChilds = [];
var loadChilds = [
            "resource"];
var resourceChilds = [
            "resource"];
var sendChilds = [];
var messageChilds = [
            "output"];
var submissionChilds = [
            "resource",
            "method",
            "header",
            "action",
            "setvalue",
            "insert",
            "delete",
            "setindex",
            "toggle",
            "setfocus",
            "dispatch",
            "rebuild",
            "recalculate",
            "revalidate",
            "refresh",
            "reset",
            "load",
            "send",
            "message"];
var methodChilds = [
            "method"];
var headerChilds = [
            "name",
            "value"];
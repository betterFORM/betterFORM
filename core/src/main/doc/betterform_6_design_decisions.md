# betterFORM 6 Design Decisions

This document tries to document the path of thinking when developing betterFORM 6.

## HTML5 syntax

### 2 versus 1 syntax

Would be possible to support two modes of operation as implicated by Web Components Spec.:

1. `<xf-input ref="foo">`
1. `<input type="text" name="foo" is="xf-input">`

First is more intuitive for existing XForms authors. 

Second aligns with the spirit of the old 'XFormsForHTML' Draft, sports native HTML5 and Web Component syntax. 

Supporting both approaches causes some difficulties in detail:

* when using 1. the `<xf-input>`element becomes a native of the host document DOM. The actual HTML control still needs to be inserted into the DOM at component creation time and MAY be part of the shadow DOM of that control
* with 2. the control is already part of the host DOM. It may still be moved into shadow DOM (-> `<content>`element in HTML templates) and may extend the native input as being a container for the label, alert, hint + help children

Example:
<pre>
`&lt;input name="foo" is="xf-input">
		&lt;xf-label>My foo label&lt;/xf-label>`
		&lt;xf-alert>My foo alert&lt;/xf-alert>
		&lt;xf-hint>My foo hint&lt;/xf-hint>
&lt;/input>`
</pre>


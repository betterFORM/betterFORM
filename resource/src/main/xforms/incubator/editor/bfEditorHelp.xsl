<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:html="http://www.w3.org/1999/xhtml"
        version="2.0" exclude-result-prefixes="html">

    <xsl:template name="help-dialog">
        <div id="bfEditorHelp" dojoType="dojox.layout.FloatingPane" title="betterFORM Editor Help" resizable="true" dockable="false" style="position:absolute;margin:10px;top:200px;left:200px;width:600px;height:350px;visibility:hidden;">
            <!--
                                <div class="bfEditorHelpTitle">betterFORM Editor</div>
            -->
            <div>The editor is fully accessible via the keyboard</div>
            <div>
                <h3>Editor Shortcuts</h3>
                <dl class="keyboard-mapping">
                    <dt class="shortcutDesc">?</dt>
                    <dd>Open Help</dd>
                </dl>
                <dl class="keyboard-mapping">
                    <dt class="shortcutDesc">ESC</dt>
                    <dd>Close Help</dd>
                </dl>
                <dl class="keyboard-mapping">
                    <dt class="shortcutDesc">t</dt>
                    <dd>Focus the XForms tree</dd>
                </dl>
            </div>
            <div>
                <h3>Tree Shortcuts</h3>
                <div class="table">
                    <div class="column1">
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">
                                <span class="shortcut">↑</span>
                            </dt>
                            <dd>Go to previous Node</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">
                                <span class="shortcut">↓</span>
                            </dt>
                            <dd>Go to next Node</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">
                                <span class="shortcut">SPACE</span>
                            </dt>
                            <dd>Select Node</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">
                                <span class="shortcut">←</span>
                            </dt>
                            <dd>Open Node</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">
                                <span class="shortcut">→</span>
                            </dt>
                            <dd>Close Node</dd>
                        </dl>
                    </div>
                    <div class="column2">
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">
                                <span class="shortcut">DEL</span>
                            </dt>
                            <dd>Delete Node</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">ALT
                                <em>and</em>
                                <span class="shortcut">↑</span>
                            </dt>
                            <dd>Move Node up</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">ALT
                                <em>and</em>
                                <span class="shortcut">↓</span>
                            </dt>
                            <dd>Move Node down</dd>
                        </dl>
                    </div>
                </div>
            </div>
        </div>
    </xsl:template>

</xsl:stylesheet>

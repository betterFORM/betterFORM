'use strict';
var LIVERELOAD_PORT = 35729;
var lrSnippet = require('connect-livereload')({port: LIVERELOAD_PORT});
var mountFolder = function (connect, dir) {
    return connect.static(require('path').resolve(dir));
};

module.exports = function(grunt) {
    // load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);

    var httpServerPort = 9001;


    var foreConfig = {
        srcDir: 'app',
        webModule: '../../../../../web',
        dist: 'dist',
        devTarget: '/target/betterform/',
        elements: '/elements'
    };

    grunt.initConfig({
        fore: foreConfig,
        webDevTarget: foreConfig.webModule + foreConfig.devTarget,
        webforms:foreConfig.webModule + '/src/main/webapp/forms',


        //WATCH tasks
        watch: {
            options: {
                nospawn: true
            },
            webcomponents:{
                files: ['*.html','*.css','*.js'],
                tasks: ['rsync:webcomponents']
            },
            forms:{
                files: ['../forms/**'],
                tasks: ['rsync:forms']
            },
            xslt:{
                files: ['../resources/xslt/webcomponents.xsl'],
                tasks: ['rsync:xslt']
            },
            bower_components:{
                files: ['../resources/xslt/webcomponents.xsl'],
                tasks: ['rsync:xslt']
            }
        },

        //RSYNC tasks
        rsync: {
            options: {
                args: ["-vpc"],
                recursive: true
            },
            webcomponents: {
                options: {
                    // !!! The last "/" is IMPORTANT here!!!!
                    src: ['./**'],
                    dest: '<%= webDevTarget %>' + 'webcomponents/'
                }
            },
            xslt: {
                options: {
                    // !!! The last "/" is IMPORTANT here!!!!
                    src: ['../resources/xslt/webcomponents.xsl'],
                    dest: '<%= webDevTarget %>' + 'WEB-INF/classes/META-INF/resources/xslt'
                }
            },
            forms:{
                options: {
                    src: '<%= webforms %>/**',
                    dest: '<%= webDevTarget %>/forms'
                }
            },
        },



        //CLEAN tasks
        clean: {
            dist: ['.tmp', '<%= fore.dist %>/*'],
            webDevTarget: {
                options: {
                    force: true
                },
                src: ['<%= webDevTarget %>/scripts', '<%= webDevTarget %>/elements', '<%= webDevTarget %>/images', '<%= webDevTarget %>/styles' ]
            }
        },

        copy: {
        },

        connect: {
            //Run "app" in grunt server
            livereload: {
                options: {
                    port: httpServerPort,
                    base:  'app',
                    keepalive:false,
                    open: true,
                    livereload: true
                }
            },
            //Testing targets
            root: {
                options: {
                    port: httpServerPort,
                    base:  '',
                    keepalive:false
                }
            },
            dev: {
                options: {
                    port: httpServerPort,
                    base:  '<%= fore.srcDir %>',
                    keepalive:false
                }
            },
            dist: {
                options: {
                    port: httpServerPort,
                    base:  '<%= fore.dist %>',
                    keepalive:false
                }
            }
        }
    });

    grunt.registerTask('default', [
        'rsync:developmentScripts',
        'rsync:developmentElements',
        'rsync:developmentPages',
        'less:development'
    ]);


    grunt.registerTask('dist', [
        'clean:dist',
        'useminPrepare',
        'imagemin',
        'concat',
        'uglify',
        'copy',
        'usemin',
        'less:dist',
        'htmlmin'
    ]);

    grunt.registerTask('server',  [
        'connect:livereload',
        'watch'
    ]);
};


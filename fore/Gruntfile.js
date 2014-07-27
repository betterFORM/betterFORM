'use strict';
var LIVERELOAD_PORT = 35729;
var lrSnippet = require('connect-livereload')({port: LIVERELOAD_PORT});
var mountFolder = function (connect, dir) {
    return connect.static(require('path').resolve(dir));
};

module.exports = function(grunt) {
    // load all grunt tasks
    require('matchdep').filterDev('grunt-*').forEach(grunt.loadNpmTasks);
    require('time-grunt')(grunt);

    var httpServerPort = 9001;

    grunt.initConfig({
        vars: grunt.file.readJSON('package.json'),

        mkdir: {
            foreForms: {
                options: {
                    mode: '0700',
                    create: ['<%= vars.webTarget %>/forms/fore/']
                }
            }
        },
        //WATCH tasks
        watch: {
            options: {
                nospawn: true,
                livereload: true
            },
            elements:{
                files: ['elements/**','fore-elements/**'],
                tasks: ['rsync:elements','rsync:distributeElements']
            },
            forms:{
                files: ['forms/**'],
                tasks: ['mkdir:foreForms','rsync:forms']
            },
            target: {
                options: {
                    livereload: true
                },
                files: ['**/*.html']
            }
        },

        connect: {
            //Run "app" in grunt server
            livereload: {
                options: {
                    port: httpServerPort,
                    base:  '.',
                    keepalive:false,
                    open: true,
                    livereload: true
                }
            }
        },

        rsync: {
            options: {
                args: ["-vpc"],
                recursive: true
            },
            elements:{
                options: {
                    src: ['elements/**','fore-elements/**'],
                    dest: 'components/'
                }
            },
            forms:{
                options: {
                    src: ['forms/**'],
                    dest: '<%= vars.webTarget %>/forms/fore/'
                }
            },
            distributeElements: {
                options: {
                    src: 'components/**',
                    dest: '<%= vars.webTarget %>/components/'
                }
            }
        }
    });

    /*
    This task must be used when deploying dev version of fore into ../web/target
    */
    grunt.registerTask('deploy', [
       'mkdir:foreForms',
       'rsync'
    ]);

    /*
    run a local server without connection to betterFORM webapp.
    */
    grunt.registerTask('server',  [
        'connect:livereload',
        'watch'
    ]);
};


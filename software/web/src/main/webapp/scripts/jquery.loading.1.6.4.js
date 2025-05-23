/**
 * Copyright (c) 2009, Nathan Bubna
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * This plugin exists to make it trivial to notify your users that
 * things are in progress.  The typical case is waiting for an
 * AJAX call to finish loading.  Just call:
 *
 *   $.loading();
 *
 * to toggle a page-wide message on and off, or you can call:
 *
 *   $('#foo').loading()
 *
 * to do the same, but locate the message within a specific element(s).
 *
 * If you want to ensure that a call doesn't errantly toggle on when
 * you meant to toggle off (or vice versa), then put a boolean value
 * as your first argument.  true is on, false is off.
 *
 *   $.loading(false); // will only ever toggle off
 *   $.loading(true, {align: 'bottom-right'});  // will only ever toggle on
 *
 * If you want a loading message to automatically go on while your
 * AJAX stuff is happening (and off when done), there's a convenient option
 * to set that up properly for you. Just do:
 *
 *   $.loading({onAjax:true, text: 'Waiting...'});
 *
 * If you want to avoid a too-quick-to-see flash of the loading message
 * in situations where the "loading" happens in a flash, then you can set
 * a delay value (in milliseconds) to block the display of the loading
 * message on all but "long" loads:
 *
 *   $.loading({onAjax:true, delay: 100});
 *
 * You can change any of the default options by altering the $.loading
 * properties (or sub-properties), like this:
 *
 *  $.loading.classname = 'loadMsg';
 *  $.loading.css.border = '1px solid #000';
 *  $.loading.working.time = 5000;
 *
 * All options can also be overridden per-call by passing in
 * an options object with the overriding properties, like so:
 *
 *  $.loading({ element:'#cancelButton', mask:true });
 *  $('#foo').loading(true, { img:'loading.gif', align:'center'});
 *
 * And if that isn't enough, this plugin supports the metadata plugin as a
 * way to specify options directly in your markup.
 *
 * Be sure to check out the provided demo for an easy overview of the most
 * commonly used options!! Of course, everything in this plugin is easy to
 * configure and/or override with those same techniques.
 *
 * To employ multiple pulse effects at once, just separate with spaces:
 *
 *  $.loading({ pulse: 'working fade', mask:true });
 *
 * Of particular note here is that it is easy to plug in additional
 * "pulse" effects.  Just add an object with a 'run' function to $.loading
 * under the desired effects name, like this:
 *
 *  $.loading.moveLeft = {
 *      time: 500,
 *      run: function(opts) {
 *          var self = this, box = opts.box;
 *          // save interval like this and it will be cleared for you
 *          // when this loading call is toggled off
 *          opts.moveLeft.interval = setInterval(function() {
 *              box.left += 1;
 *              self.animate(box);
 *          }, opts.moveLeft.time);
 *      }
 *  }
 *
 * then use it by doing something like:
 *
 *  $.loading({ pulse: 'moveLeft', align:{top:0,left:0} });
 *
 * If you add an 'end' function to that same object, then the end function
 * will be called when the loading message is turned off.
 *
 * Speaking of turning things on and off, this plugin will trigger 'loadingStart'
 * and 'loadingEnd' events when loading is turned on and off, respectively.
 * The options will, of course, be available as a second argument to functions
 * that are bound to these events.  See the demo source for an example. In
 * future versions, this plugin itself may use those events, but for now they
 * are merely notifications.
 *
 * If you are certain you only want the loading message displayed for a limited
 * period of time, you may set the 'max' option to have it automatically end
 * after the specified number of milliseconds:
 *
 *  $.loading({ text: 'Wait!', pulse: false, mask: true, max: 30000 });
 *
 * Contributions, bug reports and general feedback on this is welcome.
 *
 * @version 1.6.4
 * @name loading
 * @author Nathan Bubna
 */
;(function($) {

    // the main interface...
    var L = $.loading = function(show, opts) {
        return $('body').loading(show, opts, true);
    };
    $.fn.loading = function(show, opts, page) {
        opts = toOpts(show, opts);
        var base = page ? $.extend(true, {}, L, L.pageOptions) : L;
        return this.each(function() {
            var $el = $(this),               // support metadata
                l = $.extend(true, {}, base, $.metadata ? $el.metadata() : null, opts);
            if (typeof l.onAjax == "boolean") {
                L.setAjax.call($el, l);
            } else {
                L.toggle.call($el, l);
            }
        });
    };

    // position CSS for page opts //TODO: better support test...
    //var fixed = { position: $.browser.msie ? 'absolute' : 'fixed' };
    var fixed = { position: 'fixed' };

    // all that's extensible and configurable...
    $.extend(L, {
        version: "1.6.4",
        // commonly-used options
        align: 'top-left',
        pulse: 'working error',
        mask: false,
        img: null,
        element: null,
        text: 'Loading...',
        onAjax: undefined,
        delay: 0,
        max: 0,
        // less commonly-used options
        classname: 'loading',
        imgClass: 'loading-img',
        elementClass: 'loading-element',
        maskClass: 'loading-mask',
        css: { position:'absolute', whiteSpace:'nowrap', zIndex:1001 },
        maskCss: { position:'absolute', opacity:.15, background:'#333',
                   zIndex:101, display:'block', cursor:'wait' },
        cloneEvents: true,
        pageOptions: { page:true, align:'top-center', css:fixed, maskCss:fixed },
        // rarely-used options
        html: '<div></div>',
        maskHtml: '<div></div>',
        maskedClass: 'loading-masked',
        maskEvents: 'mousedown mouseup keydown keypress',
        resizeEvents: 'resize',

        // pulse plugin properties
        working: {
            time: 10000,
            text: 'Still working...',
            run: function(l) {
                var w = l.working, self = this;
                w.timeout = setTimeout(function() {
                    self.height('auto').width('auto').text(l.text = w.text);
                    l.place.call(self, l);
                }, w.time);
            }
        },
        error: {
            time: 100000,
            text: 'Task may have failed...',
            classname: 'loading-error',
            run: function(l) {
                var e = l.error, self = this;
                e.timeout = setTimeout(function() {
                    self.height('auto').width('auto').text(l.text = e.text).addClass(e.classname);
                    l.place.call(self, l);
                }, e.time);
            }
        },
        fade: {
            time: 800,
            speed: 'slow',
            run: function(l) {
                var f = l.fade, s = f.speed, self = this;
                f.interval = setInterval(function() {
                    self.fadeOut(s).fadeIn(s);
                }, f.time);
            }
        },
        ellipsis: {
            time: 300,
            run: function(l) {
                var e = l.ellipsis, self = this;
                e.interval = setInterval(function() {
                    var et = self.text(), t = l.text, i = dotIndex(t);
                    self.text((et.length - i) < 3 ? et + '.' : t.substring(0,i));
                }, e.time);
                function dotIndex(t) {
                    var x = t.indexOf('.');
                    return x < 0 ? t.length : x;
                }
            }
        },
        type: {
            time: 100,
            run: function(l) {
                var t = l.type, self = this;
                t.interval = setInterval(function() {
                    var e = self.text(), el = e.length, txt = l.text;
                    self.text(el == txt.length ? txt.charAt(0) : txt.substring(0, el+1));
                }, t.time);
            }
        },

        // functions
        toggle: function(l) {
            var old = this.data('loading');
            if (old) {
                if (l.show !== true) old.off.call(this, old, l);
            } else {
                if (l.show !== false) l.on.call(this, l);
            }
        },
        setAjax: function(l) {
            if (l.onAjax) {
                var self = this, count = 0, A = l.ajax = {
                    start: function() { if (!count++) l.on.call(self, l); },
                    stop: function() { if (!--count) l.off.call(self, l, l); }
                };
                this.bind('ajaxStart.loading', A.start).bind('ajaxStop.loading', A.stop);
            } else {
                this.unbind('ajaxStart.loading ajaxStop.loading');
            }
        },
        on: function(l, force) {
            var p = l.parent = this.data('loading', l);
            if (l.max) l.maxout = setTimeout(function() { l.off.call(p, l, l); }, l.max);
            if (l.delay && !force) {
                return l.timeout = setTimeout(function() {// break
                    delete l.timeout;
                    l.on.call(p, l, true);
                }, l.delay);
            }
            if (l.mask) l.mask = l.createMask.call(p, l);
            l.display = l.create.call(p, l);
            if (l.img) {
                l.initImg.call(p, l);
            } else if (l.element) {
                l.initElement.call(p, l);
            } else {
                l.init.call(p, l);
            }
            p.trigger('loadingStart', [l]);
        },
        initImg: function(l) {
            var self = this;
            l.imgElement = $('<img src="'+l.img+'"/>').bind('load', function() {
                l.init.call(self, l);
            });
            l.display.addClass(l.imgClass).append(l.imgElement);
        },
        initElement: function(l) {
            l.element = $(l.element).clone(l.cloneEvents).show();
            l.display.addClass(l.elementClass).append(l.element);
            l.init.call(this, l);
        },
        init: function(l) {
            l.place.call(l.display, l);
            if (l.pulse) l.initPulse.call(this, l);
        },
        initPulse: function(l) {
            $.each(l.pulse.split(' '), function() {
                l[this].run.call(l.display, l);
            });
        },
        create: function(l) {
            var el = $(l.html).addClass(l.classname).css(l.css).appendTo(this);
            if (l.text && !l.img && !l.element) el.text(l.originalText = l.text);
            $(window).bind(l.resizeEvents, l.resizer = function() { l.resize(l); });
            return el;
        },
        resize: function(l) {
            l.parent.box = null;
            if (l.mask) l.mask.hide();
            l.place.call(l.display.hide(), l);
            if (l.mask) l.mask.show().css(l.parent.box);
        },
        createMask: function(l) {
            var box = l.measure.call(this.addClass(l.maskedClass), l);
            l.handler = function(e) { return l.maskHandler(e, l); };
            $(document).bind(l.maskEvents, l.handler);
            return $(l.maskHtml).addClass(l.maskClass).css(box).css(l.maskCss).appendTo(this);
        },
        maskHandler: function(e, l) {
            var $els = $(e.target).parents().andSelf();
            if ($els.filter('.'+l.classname).length != 0) return true;
            return !l.page && $els.filter('.'+l.maskedClass).length == 0;
        },
        place: function(l) {
            var box = l.align, v = 'top', h = 'left';
            if (typeof box == "object") {
                box = $.extend(l.calc.call(this, v, h, l), box);
            } else {
                if (box != 'top-left') {
                    var s = box.split('-');
                    if (s.length == 1) {
                        v = h = s[0];
                    } else {
                        v = s[0]; h = s[1];
                    }
                }
                if (!this.hasClass(v)) this.addClass(v);
                if (!this.hasClass(h)) this.addClass(h);
                box = l.calc.call(this, v, h, l);
            }
            this.show().css(l.box = box);
        },
        calc: function(v, h, l) {
            var box = $.extend({}, l.measure.call(l.parent, l)),
                H = $.boxModel ? this.height() : this.innerHeight(),
                W = $.boxModel ? this.width() : this.innerWidth();
            if (v != 'top') {
                var d = box.height - H;
                if (v == 'center') {
                    d /= 2;
                } else if (v != 'bottom') {
                    d = 0;
                } else if ($.boxModel) {
                    d -= css(this, 'paddingTop') + css(this, 'paddingBottom');
                }
                box.top += d;
            }
            if (h != 'left') {
                var d = box.width - W;
                if (h == 'center') {
                    d /= 2;
                } else if (h != 'right') {
                    d = 0;
                } else if ($.boxModel) {
                    d -= css(this, 'paddingLeft') + css(this, 'paddingRight');
                }
                box.left += d;
            }
            box.height = H;
            box.width = W;
            return box;
        },
        measure: function(l) {
            return this.box || (this.box = l.page ? l.pageBox(l) : l.elementBox(this, l));
        },
        elementBox: function(e, l) {
            if (e.css('position') == 'absolute') {
                var box = { top: 0, left: 0 };
            } else {
                var box = e.position();
                box.top += css(e, 'marginTop');
                box.left += css(e, 'marginLeft');
            }
            box.height = e.outerHeight();
            box.width = e.outerWidth();
            return box;
        },
        pageBox: function(l) {
            var full = $.boxModel && l.css.position != 'fixed';
            return { top:0, left: 0,
                     height: get(full, 'Height'), width: get(full, 'Width') };
            function get(full, side) {
                var doc = document;
                if (full) {
                    var s = side.toLowerCase(), d = $(doc)[s](), w = $(window)[s]();
                    return d - css($(doc.body), 'marginTop') > w ? d : w;
                }
                var c = 'client'+side;
                return Math.max(doc.documentElement[c], doc.body[c]);
            }
        },
        off: function(old, l) {
            this.data('loading', null);
            if (old.maxout) clearTimeout(old.maxout);
            if (old.timeout) return clearTimeout(old.timeout);// break
            if (old.pulse) old.stopPulse.call(this, old, l);
            if (old.originalText) old.text = old.originalText;
            if (old.mask) old.stopMask.call(this, old, l);
            $(window).unbind(old.resizeEvents, old.resizer);
            if (old.display) old.display.remove();
            if (old.parent) old.parent.trigger('loadingEnd', [old]);
        },
        stopPulse: function(old, l) {
            $.each(old.pulse.split(' '), function() {
                var p = old[this];
                if (p.end) p.end.call(l.display, old, l);
                if (p.interval) clearInterval(p.interval);
                if (p.timeout) clearTimeout(p.timeout);
            });
        },
        stopMask: function(old, l) {
            this.removeClass(l.maskedClass);
            $(document).unbind(old.maskEvents, old.handler);
            old.mask.remove();
        }
    });

    // a few private functions...
    function toOpts(s, l) {
        if (l === undefined) {
            l = (typeof s == "boolean") ? { show: s } : s;
        } else {
            l.show = s;
        }
        // default pulse to off if doing an img
        if (l && (l.img || l.element) && !l.pulse) l.pulse = false;
        // if onAjax and they didn't specify show, default to false
        if (l && l.onAjax !== undefined && l.show === undefined) l.show = false;
        return l;
    }
    function css(el, prop) {
        var val = el.css(prop);
        return val == 'auto' ? 0 : parseFloat(val, 10);
    }
})(jQuery);

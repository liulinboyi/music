const registerRouter = require('./backend/router')

module.exports = {
  lintOnSave: false,
  css: {
    loaderOptions: {
      sass: {
        // 全局引入变量和 mixin
        additionalData: `
          @import "@/assets/scss/variable.scss";
          @import "@/assets/scss/mixin.scss";
        `
      }
    }
  },
  // webpack 4.0
  devServer: {
    host: '0.0.0.0',
    port: 8080,
    proxy: {
      '/*': {
        target: 'http://localhost:8800',
        changeOrigin: true,
        secure: false,
        logLevel: 'debug',
        onProxyRes: function (proxyRes, req, res) {
          var cookies = proxyRes.headers['set-cookie'];
          var cookieRegex = /Path=\/XXX\//i;
          //修改cookie Path
          if (cookies) {
            var newCookie = cookies.map(function (cookie) {
              if (cookieRegex.test(cookie)) {
                return cookie.replace(cookieRegex, 'Path=/');
              }
              return cookie;
            });
            //修改cookie path
            delete proxyRes.headers['set-cookie'];
            proxyRes.headers['set-cookie'] = newCookie;
          }
        }
      }
    }
    // https://v4.webpack.js.org/configuration/dev-server/#devserverbefore
    // before(app) {
    //   registerRouter(app)
    // }
  },
  configureWebpack: (config) => {
    if (process.env.npm_config_report) {
      const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin
      config.plugins.push(new BundleAnalyzerPlugin())
    }
  },
  productionSourceMap: false,
  publicPath: process.env.NODE_ENV === 'production' ? '/music-next/' : '/'
}
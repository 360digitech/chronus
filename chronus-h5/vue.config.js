// var serviceUrl = 'http://127.0.0.1:10089'
const serviceUrl = 'http://localhost:10089'

let glob = require('glob')
// 静态访问的页面
var fileValue = ''
var baseUrl = ''
if (process.env.NODE_ENV === 'development') {
  fileValue = ''
  baseUrl = '/'
} else {
  fileValue = '../'
  baseUrl = '../static/'
}
const path = require('path')
function resolve (dir) {
  return path.join(__dirname, dir)
}

function getEntry (globPath) {
  let entries = {}
  let tmp
  glob.sync(globPath).forEach(function (entry) {
    tmp = entry.split('/').splice(-3)
    console.log('加载应用：' + tmp[1])

    entries[tmp[1]] = {
      entry: 'src/' + tmp[0] + '/' + tmp[1] + '/main.js',
      template: 'public/index.html',
      filename: fileValue + tmp[1] + '.html'// 编译时打开

    }
  })
  return entries
}

let htmls = getEntry('./src/pages/**/main.js')

// 配置end
module.exports = {
  pages: htmls,
  outputDir: '../chronus-console/src/main/resources/static/static',

  publicPath: baseUrl, // 编译时打开
  devServer: {
    index: 'index.html', // 默认启动serve 打开page1页面
    open: process.platform === 'darwin',
    host: '',
    port: 8088,
    https: false,
    hotOnly: false,
    proxy: {
      '/api': {
        target: serviceUrl,
        changeOrigin: true
        // pathRewrite: function (path, req) {
        //   return path + '?shiro_sid=17b76227-3344-4164-8f30-2a6c7b9e1400'
        // }
      }
    }, // 设置代理
    before: app => { }
  },
  chainWebpack: config => {
    config.module
      .rule('images')
      .use('url-loader')
      .loader('url-loader')
      .tap(options => {
        options.limit = 10000
        return options
      })
    // set svg-sprite-loader
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()
    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
      .end()
  }
}

const Alphabet = require('alphabetjs')
const str = Alphabet('chronus h5', 'planar')
console.log(str)

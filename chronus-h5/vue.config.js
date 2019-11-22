var serviceUrl = 'http://127.0.0.1:10089'
const Alphabet = require('alphabetjs')
const str = Alphabet('chronus h5', 'planar')
console.log(str)
let glob = require('glob')
var fileValue = ''
var baseUrl = ''
if (process.env.NODE_ENV === 'development') {
  fileValue = ''
  baseUrl = '/'
} else {
  fileValue = '../../views/'
  baseUrl = '../'
}

const path = require('path')
function resolve (dir) {
  return path.join(__dirname, dir)
}

// 配置end
module.exports = {
  outputDir: '../chronus-console/src/main/resources/static',
  publicPath: baseUrl,
  devServer: {
    index: 'index.html',
    open: process.platform === 'darwin',
    host: '',
    port: 8088,
    https: false,
    hotOnly: false,
    proxy: {
      '/api': {
        target: serviceUrl,
        changeOrigin: true

      }
    }
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

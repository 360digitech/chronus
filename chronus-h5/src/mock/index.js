
const Mock = require('mockjs')
// 格式： Mock.mock( url, post/get , 返回的数据)；
Mock.mock('/api/metric/list.do', 'post', require('./json/metric.list'))
Mock.mock('/api/modules/list.do', 'post', require('./json/modules.list'))

/* eslint-disable */
import axios from 'axios'
const instance = axios.create({
  timeout: 15000,
  headers: {
    'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': 'application/json; charset=UTF-8',
    'Access-Control-Allow-Origin': '*',
    xsrfCookieName: 'XSRF-TOKEN',
    dataType: 'json'
  }
})
instance.interceptors.response.use(
  response => {
    if (response.data && response.data.flag === "S") {
      let res = response.data.data || []
      if (res instanceof Object) {
        res.__successMsg = response.data.msg // 不建议走这个字段，这个字段一般是用来做系统级异常消息
        res.__code = response.data.code // 不建议走这个字段
        res.__flag = response.data.flag // 不建议走这个字段
      } else {
        // 可能要用到格式重整合


      }
      return Promise.resolve(res)
    }
    return Promise.reject({ msg: response.data.msg, code: response.data.code, data: response.data.data })
  },
  e => {
    return Promise.reject({ message: '网络异常,请稍后再试!' })
  }
)
export default instance

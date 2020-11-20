/* eslint-disable */
import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
//import store from './store'

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


// 添加请求拦截器
// instance.interceptors.request.use(function (config) {
//     let token = localStorage.getItem('token')
//     if (token) {
//         config.headers = {
//             'Authorization': token,
//         }
//     }
//     return config;
// }, function (error) {
//     return Promise.reject(error);
// });

instance.interceptors.response.use(
    response => {
        if (response.data && response.data.flag === "S") {
            // 登录失效
            if (response.data.code === '407') {
                MessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确定登出', {
                    confirmButtonText: '重新登录',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // store.dispatch('LogOut').then(() => {
                    //     location.reload() // 为了重新实例化vue-router对象 避免bug
                    // })
                    //store.dispatch('LogOut')
                    // const {
                    //     protocol,
                    //     host,
                    // } =  window.parent.location

                    // window.parent.location.href = `${protocol}//${host}/login`

                }).catch(() => {
                    return Promise.reject({msg: response.data.msg, code: response.data.code, data: response.data.data})
                });
            }

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
        return Promise.reject({msg: response.data.msg, code: response.data.code, data: response.data.data})
    },
    error => {
        return Promise.reject(error)
    }
);
export default instance


import Vuex from 'vuex'
import axios from 'axios'
import Vue from 'vue'
Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    status: '',
    userName: localStorage.getItem('userName') || ''
  },
  mutations: {
    auth_request (state) {
      state.status = 'loading'
    },
    auth_success (state) {
      state.status = 'success'
    },
    auth_user (state, userName) {
      state.status = 'success'
      state.userName = userName
    },
    auth_error (state) {
      state.status = 'error'
    },
    logout (state) {
      state.status = ''
      state.userName = ''
    }
  },
  actions: {
    Login ({commit}, user) {
      return new Promise((resolve, reject) => {
        commit('auth_request')
        // 向后端发送请求，验证用户名密码是否正确，请求成功接收后端返回的token值，利用commit修改store的state属性，并将token存放在localStorage中
        console.log(user)
        axios.post('/api/login', user)
          .then(resp => {
            if (resp.data.flag === 'S') {
              resolve(resp.data.data)
            } else {
              reject(resp.data.msg)
            }
            localStorage.setItem('userName', resp.data.data)
            commit('auth_user', resp.data.data.name)
          })
          .catch(err => {
            commit('auth_error')
            localStorage.removeItem('userName')
            reject(err)
          })
      })
    },
    LogOut ({ commit }) {
      return new Promise(resolve => {
        localStorage.removeItem('userName')
        commit('logout')
        resolve()
      })
    }
  },
  getters: {
    // !!将state.token强制转换为布尔值，若state.token存在且不为空(已登录)则返回true，反之返回false
    isLoggedIn: state => !!state.token,
    authStatus: state => state.status
  }
})

export default store

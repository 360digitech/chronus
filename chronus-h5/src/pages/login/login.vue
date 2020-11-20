<template>
  <div class="login-container">

    <el-form ref="loginForm" :model="loginForm" class="login-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <el-row type="flex" align="middle" justify="center" class="title">
          <img src='../../../static/img/logo_white.png' style="width:350px;margin-right:10px" />
        </el-row>
      </div>
      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input v-model.trim="loginForm.userNo"  name="username" type="text"
          auto-complete="on" />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input :type="passwordType" v-model="loginForm.pwd"  name="password"
          auto-complete="on" @keyup.enter.native="handleLogin" />
        <span class="show-pwd" @click="showPwd">
          <svg-icon icon-class="eye" />
        </span>
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;"
        @click.native.prevent="handleLogin">{{ $t('login.logIn') }}</el-button>

      <el-row type="flex" justify="center" v-if='errorMsg!=""'>
        <span style="color: red;">{{errorMsg}}</span>
      </el-row>

      <!--<div style="position:relative">

        <el-button class="thirdparty-button" type="primary" @click="showDialog=true">
          其他登录方式
        </el-button>
      </div>-->
    </el-form>

    <!--<el-dialog title="其他登录方式" :visible.sync="showDialog">
        <div type="primary" v-for="item in loginWay" :key="item.id">
        <el-link :href="item.url" >{{item.name}}</el-link>
        </div>
    </el-dialog>-->

  </div>
</template>

<script>

export default {
  name: 'Login',

  data () {
    return {
      loginForm: {
        userNo: '',
        pwd: ''
      },
      // loginWay: [],
      passwordType: 'password',
      errorMsg: '',
      loading: false
      // showDialog: false,
      // redirect: undefined

    }
  },
  mounted () {
    // this.getOtherLoginWay()
    // 检查登录状态 如果是登录态 直接跳转到主页
    // if (this.$store.getters.isLoggedIn) {
    //   this.skipToHome()
    // }
  },

  // destroyed () {
  // },

  methods: {
    skipToHome () {
      const {
        protocol,
        host
      } = window.location
      window.location.href = `${protocol}//${host}/index`
    },

    showPwd () {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
    },

    handleLogin () {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.errorMsg = ''
          this.$store.dispatch('Login', this.loginForm)
            .then(() => {
              // this.$router.push({ path: '/' })
              this.skipToHome()
            })
            .catch((error) => {
              this.errorMsg = error
            }).finally(() => {
              this.loading = false
            })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    }
    // getOtherLoginWay () {
    //   this.$http.get('/api/loginWay').then(res => {
    //     this.loginWay = res
    //   })
    // }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">

$bg: #283443;
$light_gray: #eee;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
    &::first-line {
      color: $light_gray;
    }
  }
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;
    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;
      &:-webkit-autofill {
        // eslint-disable-next-line
        -webkit-box-shadow: 0 0 0px 1000px $bg inset !important;
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }
  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  position: fixed;
  height: 100%;
  width: 100%;
  background-color: $bg;
  .login-form {
    position: absolute;
    left: 0;
    right: 0;
    width: 520px;
    max-width: 100%;
    padding: 35px 35px 15px 35px;
    margin: 120px auto;
  }
  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;
    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }
  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }
  .title-container {
    position: relative;
    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
    .set-language {
      color: #fff;
      position: absolute;
      top: 5px;
      right: 0px;
    }
  }
  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
    .thirdparty-button {
    position: absolute;
    right: 0;
    bottom: 6px;
  }
}
</style>

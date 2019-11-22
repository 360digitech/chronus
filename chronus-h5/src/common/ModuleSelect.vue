<template>
  <div>
    <!-- <el-select v-model="currentValue" placeholder="" @change="sendChange" size="small">
      <el-option v-for="(item,index) in modules" :key="index" :label="item.displayName" :value="item.pid"></el-option>
    </el-select> -->

    <el-cascader ref='cascaderClass' style="width:100%" v-model="currentValue" :options="modules"  :props="props" @change="sendChange" size="small" clearable filterable></el-cascader>

  </div>
</template>

<script>
import http from '../Http'

export default {
  name: 'module-select',
  props: {
    value: Number
  },
  data () {
    return {
      modules: [],
      currentValue: [],
      props: {
        label: 'displayName',
        value: 'id'
      }
    }
  },

  mounted () {
    this.initData()
  },
  methods: {
    initData: function () {
      http.post('/api/module/all.do').then(result => {
        this.modules = result
        this.modules.forEach(row => {
          row.children.forEach(item => {
            if (item.id === this.value) {
              this.currentValue = [row.id, item.id]
            }
          })
        })
      }).catch(reason => {
        console.log(reason)
      })
    },
    childrenMethod () {
      alert(123)
    },

    sendChange: function () {
      var currentLabels = ''
      this.modules.forEach(row => {
        if (row.id === this.currentValue[0]) {
          row.children.forEach(item => {
            if (item.id === this.currentValue[1]) {
              currentLabels = row.displayName + '-' + item.displayName
            }
          })
        }
      })
      this.$emit('moduleNameChange', currentLabels)
      this.$emit('input', this.currentValue[1])
    }

  },
  watch: {
    value (val, oldVal) {
      if (val === null) {
        this.currentValue = []
        return
      }
      console.log(this.modules)
      this.modules.forEach(row => {
        row.children.forEach(item => {
          if (item.id === val) {
            this.currentValue = [row.id, item.id]
          }
        })
      })
    }
  }
}
</script>

<style>
</style>

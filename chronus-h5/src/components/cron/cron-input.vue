<template>
  <div>
    <el-popover v-model="visible" popper-class="cron_c">
      <cron v-model="cron_" :size="size" @change="change"/>
      <el-input slot="reference" v-model="cron_" :placeholder="$t('common.inputPlaceholder')" :size="size">
        <el-button slot="append" icon="el-icon-refresh" @click="reset"/>
      </el-input>
    </el-popover>
  </div>
</template>

<script>
import './styles/global.less'
import Cron from './cron'
export default {
  name: 'CronInput',
  components: {
    Cron
  },
  props: {
    value: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: 'medium'
    }
  },
  data () {
    return {
      cron_: '',
      visible: false
    }
  },
  watch: {
    value (val) {
      if (val) {
        this.setCron(val)
      }
    }
  },
  created () {
    if (this.value) {
      this.setCron(this.value)
    }
  },
  methods: {
    setCron (newValue) {
      if (!newValue || newValue.trim().length < 11) {
        this.$message.error(this.$t('common.wordNumError'))
        return
      }
      this.cron_ = newValue
    },
    change (cron) {
      this.cron_ = cron
      this.$emit('change', cron)
    },
    reset () {
      this.$emit('reset', this.value)
    }
  }
}
</script>

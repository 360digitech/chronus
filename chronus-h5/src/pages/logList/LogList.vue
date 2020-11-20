<template>
  <div style="padding:20px">

    <el-form :inline="true" @submit.native.prevent size="medium">
      <el-form-item label="系统">
        <el-select v-model="queryObj.sysCode" placeholder="请选择" clearable filterable>
          <el-option v-for="item in allSysCodes" :key="item" :label="item" :value="item">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="任务名称">
        <el-input size="medium" v-model.trim="queryObj.taskName" placeholder="请输入内容" clearable style="width:200px"
          @keyup.enter.native="onQuery"></el-input>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker v-model="timeRange" type="datetimerange" :picker-options="pickerOptions" range-separator="至"
          start-placeholder="开始日期" end-placeholder="结束日期" align="right" >
        </el-date-picker>
      </el-form-item>

      <el-form-item>
        <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
      v-loading="tableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
        <el-table-column prop="sysCode" label="系统"  min-width="150"></el-table-column>
        <el-table-column prop="taskName" label="任务" min-width="150"></el-table-column>
        <el-table-column prop="execAddress" label="执行机" min-width="150"></el-table-column>
        <el-table-column prop="cluster" label="执行集群" width="70"></el-table-column>
        <el-table-column prop="reqNo" label="流水号" min-width="270"></el-table-column>
        <el-table-column prop="startDate" label="开始时间" :formatter="dateFormat" width="140"></el-table-column>
        <el-table-column prop="endDate" label="结束时间" :formatter="dateFormat" width="140"></el-table-column>
        <el-table-column prop="handleTotalCount" label="总数" width="50"></el-table-column>
        <el-table-column prop="handleFailCount" label="失败数" width="60"></el-table-column>
        <el-table-column prop="dateCreated" label="保存时间" :formatter="dateFormat" width="140"></el-table-column>
    </el-table>

    <el-row type="flex" justify="center" style="margin-top:15px;">
      <el-pagination @current-change="onQuery" :current-page.sync="queryObj.pageNum" :page-size="queryObj.pageSize"
        layout="total, prev, pager, next" :total="total">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
import moment from 'moment'

export default {

  data () {
    return {
      tableLoading: false,
      dialogBatchShow: false,
      batchOperationFalg: false,

      queryObj: {
        sysCode: '',
        taskName: '',
        startDate: '',
        endDate: '',
        pageNum: 0,
        pageSize: 15

      },
      currentObject: {

      },
      dataList: [],
      total: 0,
      totalData: [],
      timeRange: [moment().subtract(5, "m"), moment()],
      allSysCodes: [],
      pickerOptions: {
        shortcuts: [
          {
            text: '最近半小时',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 1800 * 1000)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一小时',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一天',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近三天',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 3)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '最近一周',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          }]
      }

    }
  },
  mounted () {
    this.loadAllSysCodes()
    this.onQuery()
    // 监听stg的变化
    window.addEventListener('message', e => {
      if (typeof e.data === 'string') this.onQuery()
    }, false)
  },
  methods: {
    loadAllSysCodes () {
      this.$http.get('/api/system/loadAllSysCodes').then(res => {
        this.allSysCodes = res
      })
    },
    dateFormat: function (row, column) {
      var date = row[column.property]
      if (date === undefined) {
        return ''
      }
      return moment(date).format('YYYY-MM-DD HH:mm:ss')
    },
    dateFormatVal: function (date) {
      if (date === undefined) {
        return ''
      }
      return moment(date).format('YYYY-MM-DD HH:mm:ss')
    },
    onQuery () {
      this.tableLoading = true
      this.dataList = []
      if (this.timeRange) {
        this.queryObj.startDate = moment(this.timeRange['0']).format('YYYY-MM-DD HH:mm:ss')
        this.queryObj.endDate = moment(this.timeRange['1']).format('YYYY-MM-DD HH:mm:ss')
      }
      this.$http.post('/api/log/getAll', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res.list
        this.total = res.total
      })
    }
  }

}
</script>

<style scoped>
.tableHeader {
  background-color: red !important;
}
</style>

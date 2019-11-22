<template>
  <div style="padding:20px">

    <el-form :inline="true" @submit.native.prevent>
      <el-form-item label="">
        <el-input size="medium" v-model="queryObj.groupName" placeholder="分组名称" clearable style="width:300px"
          @keyup.enter.native="onQuery"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
        <el-button size="medium" type="success" @click="newObject">新建</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
      v-loading="tableLoading" size="mini" @row-dblclick="onShow"
      :header-cell-style="{background:'#F3F4F7',color:'#555'}" row-key='id'>
      <el-table-column property="groupName" label="分组编码" width="120" align='center'>
      </el-table-column>
      <el-table-column property="groupDesc" label="分组描述" align="center">
      </el-table-column>
      <el-table-column property="sysCode" label="系统编号" align="center" width="220">
      </el-table-column>
      <el-table-column property="sysDesc" label="系统描述" align="center" >
      </el-table-column>
      <el-table-column property="dateUpdated" label="更新时间" align="center" width="160" :formatter="dateFormat">
      </el-table-column>
      <el-table-column property="updatedBy" label="更新人" align="center" width="160">
      </el-table-column>
      <el-table-column property="operation" fixed="right" label="操作" align="center" width="150">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="editObject(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteObject(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-row type="flex" justify="center" style="margin-top:15px;">
      <el-pagination background layout="total, prev, pager, next, jumper" :current-page.sync="queryObj.pageNum"
        @current-change="onQuery" :total="total" :page-size="queryObj.pageSize">
      </el-pagination>
    </el-row>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px">
      <el-form label-position='right' label-width="110px">
        <el-form-item label="分组编码">
          <el-input v-model="currentObject.groupName"></el-input>
        </el-form-item>
        <el-form-item label="分组描述">
          <el-input v-model="currentObject.groupDesc"></el-input>
        </el-form-item>
        <el-form-item label="系统编号">
          <el-input v-model="currentObject.sysCode"></el-input>
        </el-form-item>
        <el-form-item label="系统描述">
          <el-input v-model="currentObject.sysDesc"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>

    </el-dialog>

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
        groupName: '',
        pageNum: 1,
        pageSize: 10

      },
      currentObject: {

      },
      dataList: [],
      total: 0,
      totalData: [],

      dialogVisible: false,
      dialogLoading: false,
      dialogTitle: '',
      EditType: 'new'

    }
  },
  mounted () {
    this.onQuery()
  },
  methods: {
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
      this.$http.post('/api/system/getAllGroup', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res.list
        this.total = res.total
      })
    },
    newObject () {
      this.dialogTitle = '新建系统分组'
      this.dialogVisible = true
      this.EditType = 'new'
      this.currentObject = {}
    },

    editObject (row) {
      this.dialogTitle = '编辑系统分组'
      this.dialogVisible = true
      this.EditType = 'edit'
      this.currentObject = JSON.parse(JSON.stringify(row))
    },
    save () {
      var url = ''
      if (this.EditType === 'new') {
        url = '/api/system/'
        this.$http.post(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      } else {
        url = '/api/system/'
        this.$http.put(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      }
    },
    deleteObject (row) {
      debugger
      this.$http.delete('/api/system/', {data: row}).then(res => {
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    onShow (row) {
      this.currentObject = row
      this.detailShow = true
    }
  }

}
</script>

<style scoped>
.tableHeader {
  background-color: red !important;
}
</style>

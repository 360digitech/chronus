<template>
    <div style="padding:20px">

        <el-form :inline="true" @submit.native.prevent>
            <!--<el-form-item label="">
                <el-input size="medium" v-model="queryObj.tag" placeholder="请输入内容" clearable style="width:300px"
                          @keyup.enter.native="onQuery"></el-input>
            </el-form-item>-->

            <el-form-item>
                <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
                <el-button size="medium" type="success" @click="newObject">新建</el-button>
            </el-form-item>
        </el-form>

        <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
                  v-loading="tableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}"
                  row-key='id'>
            <el-table-column property="groupName" label="分组名" min-width="120" align='center'>
            </el-table-column>
            <el-table-column property="groupDesc" label="分组描述" align="center" min-width="100">
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

        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="650px">
            <el-form label-position='right' label-width="110px" size="medium">
                <el-form-item label="分组名">
                    <el-input v-model.trim="currentObject.groupName" :disabled="this.EditType==='edit'?true:false" maxlength="32" show-word-limit></el-input>
                </el-form-item>
                <el-form-item label="分组描述">
                    <el-input v-model.trim="currentObject.groupDesc"></el-input>
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
        tag: '',
        pageNum: 0,
        pageSize: 10

      },
      currentObject: {},
      dataList: [],
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
      this.$http.post('/api/group/getAll', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res
      })
    },
    newObject () {
      this.dialogTitle = '新建分组'
      this.dialogVisible = true
      this.EditType = 'new'
      this.currentObject = {}
    },

    editObject (row) {
      this.dialogTitle = '编辑分组'
      this.dialogVisible = true
      this.EditType = 'edit'
      this.currentObject = JSON.parse(JSON.stringify(row))
    },
    save () {
      var url = ''
      if (this.EditType === 'new') {
        url = '/api/group/'
        this.$http.post(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        }).catch(errors => {
          this.$message.error(errors.msg)
        })
      } else {
        url = '/api/group/'
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
      this.$http.delete('/api/group/' + row.groupName).then(res => {
        this.dialogVisible = false
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
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

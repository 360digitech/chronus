<template>
  <div style="padding:20px">

    <el-form :inline="true" @submit.native.prevent>
      <el-form-item label="用户名">
        <el-input size="medium" v-model="queryObj.userNo" placeholder="用户名" clearable style="width:200px"
          @keyup.enter.native="onQuery"></el-input>
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="queryObj.roleNo" placeholder="请选择" clearable filterable>
          <el-option label="admin" value="admin"></el-option>
          <el-option label="guest" value="guest"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="组别">
        <el-select v-model="queryObj.groups" placeholder="请选择" clearable filterable>
          <el-option v-for="item in groupList" :key="item.id" :label="item.groupDesc" :value="item.groupName">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button size="medium" type="primary" @click="onQuery">查询</el-button>
        <el-button size="medium" type="success" @click="newObject">新建</el-button>
      </el-form-item>
    </el-form>

    <el-table ref="singleTable" :data="dataList" highlight-current-row border style="width: 100%"
      v-loading="tableLoading" size="mini" :header-cell-style="{background:'#F3F4F7',color:'#555'}"
      row-key='id'>
      <el-table-column property="userNo" label="用户名" min-width="120" align='center'>
      </el-table-column>
      <el-table-column property="name" label="姓名" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="email" label="邮箱" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="roleNo" label="角色" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="group" label="组别" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="state" label="状态" align="center" min-width="100">
      </el-table-column>
      <el-table-column property="dateUpdated" label="更新时间" align="center" width="160" :formatter="dateFormat">
      </el-table-column>
      <el-table-column property="updatedBy" label="更新人" align="center" width="160">
      </el-table-column>
      <el-table-column property="operation" fixed="right" label="操作" align="center" width="300">
        <template slot-scope="scope">
          <el-button v-if="scope.row.state==='N'" plain size="mini" type="success" @click.stop="changeState(scope.row,'Y')">生效</el-button>
          <el-button v-if="scope.row.state==='Y'" plain size="mini" type="warning" @click.stop="changeState(scope.row,'N')">失效</el-button>
          <el-button size="mini" type="primary" @click.stop="editObject(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click.stop="deleteObject(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

        <el-row type="flex" justify="center" style="margin-top:15px;">
      <el-pagination @current-change="onQuery" :current-page.sync="queryObj.pageNum" :page-size="queryObj.pageSize"
        layout="total, prev, pager, next" :total="total">
      </el-pagination>
    </el-row>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="550px" @close='closeDialog'>
      <el-form label-position='right' label-width="110px">
        <el-form-item label="用户名">
          <el-input v-model.trim="currentObject.userNo" :disabled="this.EditType==='edit'?true:false" style="width:200px"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model.trim="currentObject.name" style="width:200px"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model.trim="currentObject.email" style="width:200px"></el-input>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="currentObject.roleNo" placeholder="请选择" clearable filterable>
          <el-option label="admin" value="admin"></el-option>
          <el-option label="guest" value="guest"></el-option>
        </el-select>
        </el-form-item>
        <el-form-item label="组别">
                  <el-select v-model="currentObject.groups" placeholder="请选择" multiple  clearable filterable>
          <el-option v-for="item in groupList" :key="item.id" :label="item.groupDesc" :value="item.groupName">
          </el-option>
        </el-select>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="closeDialog">取 消</el-button>
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
      dialogTitle: '新建集群',
      dialogVisible: false,
      EditType: 'new',
      queryObj: {
        userNo: '',
        roleNo: '',
        group: '',
        pageNum: 0,
        pageSize: 10
      },
      currentObject: {
        userNo: '',
        roleNo: '',
        group: '',
        groups: []
      },
      groupList: [
        {
          groupName: '',
          groupDesc: ''
        }
      ],
      runtimeObj: {},
      dataList: [],
      total: 0,
      totalData: [],
      allSysCodes: []
    }
  },
  mounted () {
    this.onQuery()
    this.getAllGroup()
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
      this.$http.post('/api/user/getAllUser', this.queryObj).then(res => {
        this.tableLoading = false
        this.dataList = res.list
        this.total = res.total
      })
    },
    newObject () {
      this.dialogTitle = '新增用户'
      this.dialogVisible = true
      this.EditType = 'new'
      this.currentObject = {}
    },
    editObject (row) {
      this.dialogTitle = '编辑集群'
      this.dialogVisible = true
      this.EditType = 'edit'
      this.currentObject = {}
      row.group = ''
      this.currentObject = JSON.parse(JSON.stringify(row))
    },
    save () {
      var url = ''
      if (this.EditType === 'new') {
        url = '/api/user/'
        this.$http.post(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        })
      } else {
        url = '/api/user/'
        this.$http.put(url, this.currentObject).then(res => {
          this.dialogVisible = false
          this.$message.success('操作成功！')
          this.onQuery()
        })
      }
    },
    closeDialog () {
      this.dialogVisible = false
      this.onQuery()
    },
    deleteObject (row) {
      this.$http.delete('/api/user/' + row.userNo).then(res => {
        this.dialogVisible = false
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    changeState (row, state) {
      debugger
      row.state = state
      console.log(row)
      this.$http.put('/api/user/', row).then(res => {
        this.$message.success('操作成功！')
        this.onQuery()
      }).catch(errors => {
        this.$message.error(errors.msg)
      })
    },
    onShow (row) {
      this.currentObject = row
    },
    getAllGroup () {
      this.$http.post('/api/group/getAll').then(res => {
        this.groupList = res
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

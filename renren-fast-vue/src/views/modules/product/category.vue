<template>
  <div>
    <el-tree :data="menus" :props="defaultProps" :expand-on-click-node="false" show-checkbox node-key="catId"
             :default-expanded-keys="expandKeys" draggable
             :allow-drop="allowDrop" @node-drop="handleDrop">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="text"
            size="mini"
            v-if="node.level==1||node.level==2"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
            type="text"
            size="mini"
            v-if="node.childNodes.length==0"
            @click="() => remove(node, data)">
            Delete
          </el-button>
           <el-button
             type="text"
             size="mini"
             @click="() => edit(data)">
            Edit
          </el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form :model="category" ref="categoryForm">
        <el-form-item label="分类名">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitData">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  data () {
    return {
      menus: [],
      title: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      expandKeys: [],
      dialogVisible: false,
      category: {name: '', parentCid: 0, catLevel: 0, showStatus: 1, sort: 0, catId: null, icon: '', productUnit: ''},
      dialogType: '',
      updateNodes: []
    }
  },
  created () {
    this.getMenus()
  },
  methods: {
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        console.log('菜单数据', data)
        this.menus = data.entities
      })
    },
    append (data) {
      console.log('append()', data)
      this.dialogType = 'add'
      this.title = '添加分类'
      this.dialogVisible = true
      this.resetCategory()
      this.category.parentCid = data.catId
      this.category.catLevel = data.catLevel * 1 + 1
    },
    resetCategory () {
      this.category = {
        name: '',
        parentCid: 0,
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        catId: null,
        icon: '',
        productUnit: ''
      }
    },
    edit (data) {
      this.dialogType = 'edit'
      this.title = '修改分类'
      this.dialogVisible = true
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: 'get'
      }).then(({data}) => {
        console.log('分类数据', data)
        this.category = data.category
      })
    },
    remove (node, data) {
      console.log('remove()', node, data)
      let ids = [data.catId]
      this.$confirm(`此操作将永久删除该文件${data.name}, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          console.log('删除结果', data)
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
          this.getMenus()
          this.expandKeys = [node.parent.data.catId]
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    addCategory () {
      console.log('this.category:' + this.category)
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'post',
        data: this.$http.adornData(this.category, false)
      }).then(({data}) => {
        console.log('菜单保存成功')
        this.getMenus()
        this.dialogVisible = false
        this.expandKeys = [this.category.parentCid]
      })
    },
    submitData () {
      if (this.dialogType === 'add') {
        this.addCategory()
      } else if (this.dialogType === 'edit') {
        this.editCategory()
      }
    },
    editCategory () {
      this.$http({
        url: this.$http.adornUrl('/product/category/update'),
        method: 'post',
        data: this.$http.adornData(this.category, false)
      }).then(({data}) => {
        console.log('菜单保存成功')
        this.getMenus()
        this.dialogVisible = false
        this.expandKeys = [this.category.parentCid]
      })
    },
    allowDrop (draggingNode, dragNode, type) {
      // 1. 被拖动的当前节点总层数和所在父节点总层数不能大于3
      // console.log(draggingNode, dragNode, type)
      let draggingNodeCount = this.countNodeLevel(draggingNode)
      if (type === 'inner') {
        return dragNode.level + draggingNodeCount <= 3
      } else {
        return dragNode.parent.level + draggingNodeCount <= 3
      }
    },
    // 统计节点总层级
    countNodeLevel (draggingNode) {
      // console.log(draggingNode.level)
      let totalCount = 0
      totalCount++
      if (draggingNode.childNodes != null && draggingNode.childNodes.length > 0) {
        let maxCount = 0
        for (let index in draggingNode.childNodes) {
          let count = this.countNodeLevel(draggingNode.childNodes[index])
          maxCount = Math.max(maxCount, count)
        }
        return totalCount + maxCount
      } else {
        return totalCount
      }
    },
    handleDrop (draggingNode, dragNode, type, ev) {
      console.log(draggingNode, dragNode, type, ev)
      // 重置
      this.updateNodes = []
      let nowParentCid = 0
      let siblings
      if (type === 'before' || type === 'after') {
        if (dragNode.parent) {
          nowParentCid = dragNode.parent.data.catId
        }
        siblings = dragNode.parent.childNodes
      } else {
        nowParentCid = dragNode.data.catId
        siblings = dragNode.childNodes
      }
      for (let i in siblings) {
        let catId = siblings[i].data.catId
        let sort = i
        if (siblings[i].data.catId === draggingNode.data.catId) {
          let catLevel = draggingNode.level
          if (siblings[i].level !== draggingNode.level) {
            catLevel = siblings[i].level
            this.updateChildNodeLevel(siblings[i])
          }
          this.updateNodes.push({catId, sort, parentCid: nowParentCid, catLevel})
        } else {
          this.updateNodes.push({catId, sort})
        }
      }
      this.$http({
        url: this.$http.adornUrl('/product/category/updateBatch'),
        method: 'post',
        data: this.$http.adornData(this.updateNodes, false)
      }).then(({data}) => {
        this.$message.success('菜单修改成功')
        this.getMenus()
        this.expandKeys = [nowParentCid]
      })
    },
    updateChildNodeLevel (node) {
      if (node.childNodes) {
        for (let i in node.childNodes) {
          let catId = node.childNodes[i].data.catId
          let catLevel = node.childNodes[i].level
          this.updateNodes.push({catId, catLevel})
          this.updateChildNodeLevel(node.childNodes[i])
        }
      }
    }

  }
}
</script>
<style>

</style>

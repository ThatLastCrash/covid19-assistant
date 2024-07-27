// 查询列表接口
const getPicturePage = (params) => {
  return $axios({
    url: '/picture/page',
    method: 'get',
    params
  })
}

// 删除接口
const deletePicture = (ids) => {
  return $axios({
    url: '/picture',
    method: 'delete',
    params: { ids }
  })
}

// 修改接口
const editPicture = (params) => {
  return $axios({
    url: '/picture',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
const addPicture = (params) => {
  return $axios({
    url: '/picture',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情
const queryPictureById = (id) => {
  return $axios({
    url: `/picture/${id}`,
    method: 'get'
  })
}

// 获取菜品分类列表
const getPictureList = (params) => {
  return $axios({
    url: '/picture/list',
    method: 'get',
    params
  })
}
// 查询列表接口
const getPictureAllPage = (params) => {
  return $axios({
    url: '/picture/all',
    method: 'get',
    params
  })
}
// 查菜品列表的接口
const queryPictureList = (params) => {
  return $axios({
    url: '/picture/list',
    method: 'get',
    params
  })
}

// 文件down预览
const commonDownload = (params) => {
  return $axios({
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    },
    url: '/common/download',
    method: 'get',
    params
  })
}

// 起售停售---批量起售停售接口
const pictureStatusByStatus = (params) => {
  return $axios({
    //url: `/picture/status/${params.status}`,
    url: `/picture/status`,
    method: 'put',
    params: { ids: params.id }
  })
}
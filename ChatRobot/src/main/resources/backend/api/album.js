// 查询列表数据
const getAlbumPage = (params) => {
  return $axios({
    url: '/album/page',
    method: 'get',
    params
    //url: '/api/v1/albums/1',
    // method: 'get',
    // params
  })
}
// 查询列表数据
const getAlbumAllPage = (params) => {
  return $axios({
    url: '/album/all',
    method: 'get',
    params
    //url: '/api/v1/albums/1',
    // method: 'get',
    // params
  })
}
// 删除数据接口
const deleteAlbum = (ids) => {
  return $axios({
    url: '/album',
    method: 'delete',
    params: { ids }
  })
}

// 修改数据接口
const editAlbum = (params) => {
  return $axios({
    url: '/album',
    method: 'put',
    data: { ...params }
  })
}

// 新增数据接口
const addAlbum = (params) => {
  return $axios({
    url: '/album',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情接口
const queryAlbumById = (id) => {
  return $axios({
    url: `/album/${id}`,
    method: 'get'
  })
}

// 批量起售禁售
const albumStatusByStatus = (params) => {
  return $axios({
    url: `/album/status/${params.status}`,
    method: 'post',
    params: { ids: params.ids }
  })
}

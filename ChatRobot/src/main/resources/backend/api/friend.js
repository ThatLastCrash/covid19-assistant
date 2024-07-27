// 查询列表接口
const getFriendPage = (params) => {
  return $axios({
    url: '/friend/page',
    method: 'get',
    params
  })
}
// 删除接口
const deleteFriend = (ids) => {
  return $axios({
    url: '/friend',
    method: 'delete',
    params: { ids }
  })
}

// 新增接口
const addFriend = (params) => {
  return $axios({
    url: '/friend',
    method: 'post',
    data: { ...params }
  })
}

// 修改接口
const editFriend = (params) => {
  return $axios({
    url: '/friend',
    method: 'put',
    data: { ...params }
  })
}



// 查询详情
const queryFriendById = (id) => {
  return $axios({
    url: `/friend/${id}`,
    method: 'get'
  })
}



// 查菜品列表的接口
const queryDishList = (params) => {
  return $axios({
    url: '/dish/list',
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
const friendStatusByStatus = (params) => {
  return $axios({
    url: `/friend/status/${params.status}`,
    method: 'post',
    params: { ids: params.id }
  })
}
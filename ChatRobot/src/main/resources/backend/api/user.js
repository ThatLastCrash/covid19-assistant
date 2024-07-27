function getUserInfo (id) {
  return $axios({
    url: `/user/${id}`,
    method: 'get'
  })
}
const getUserPage = (params) => {
  return $axios({
    url: '/user/page',
    method: 'get',
    params
  })
}
// 修改---添加员工
function editInfo (params) {
  return $axios({
    url: '/user',
    method: 'put',
    data: { ...params }
  })
}

// 修改页面反查详情接口
function queryUserByName (username) {
  return $axios({
    url: `/user?username=`+username,
    method: 'get'
  })
}
// 修改---启用禁用接口
function enableOrDisableEmployee (params) {
  return $axios({
    url: '/employee',
    method: 'put',
    data: { ...params }
  })
}

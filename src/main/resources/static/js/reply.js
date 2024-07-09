// async : 함수가 비동기적으로 실행된다는 것을 나타냄 -- 해당 함수가 호출되면, 반환값은 항상 "Promise" 객체
async function get1(bno) {
    // 'await' 키워드를 사용하여 axios.get 요청을 수행하고, 응답이 올 때까지 기다림
    // `/replies/list/${bno}` 경로로 GET 요청(axios.get())을 보내며, 'bno'는 게시물 번호를 의미
    const result = await axios.get(`/replies/list/${bno}`);

    // 서버로부터 받은 응답(result)을 콘솔에 출력
    // console.log(result);

    return result; // 화면에서 결과를 표시할 수 있도록 호출 결과를 반환
}

/* async function getList({bno, page, size, goLast}){
    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}});
    return result.data;
} */

// 비동기적으로 특정 게시물의 댓글 목록을 가져오며, 만약 goLast 플래그가 true로 설정되어 있으면 마지막 페이지의 댓글 목록을 가져오는 기능을 포함
async function getList({bno, page, size, goLast}) {
    // bno : 게시물 번호
    // page : 요청할 페이지 번호
    // size : 한 페이지에 표시할 댓글 수
    // goLast : 마지막 페이지로 이동할지 여부를 결정하는 플래그

    // 주어진 게시물 번호(bno)에 해당하는 댓글 목록을 서버에 요청
    // 요청 URL /replies/list/${bno}
    // 쿼리 매개변수 page, size 전달
    const result = await axios.get(`/replies/list/${bno}`, {params: {page, size}})

    // 만약 goLast가 ture이면,
    if (goLast) {
        const total = result.data.total // -- 전체 댓글 수(total) 가져옴
        const lastPage = parseInt(Math.ceil(total / size)) // -- 마지막 페이지 번호 계산

        // 마지막 페이지 번호로 다시 getList 함수 호출
        return getList({bno: bno, page: lastPage, size: size})

    }

    // 만약 goLast가 false거나 마지막 페이지 댓글 목록을 가져온 경우, 결과 데이터를 반환
    return result.data
}

// 새로운 댓글을 등록하는 기능 추가 -- 파라미터를 JS의 객체로 받아 axios.post()를 이용해 전달
async function addReply(replyObj) {
    const response = await axios.post(`/replies/`, replyObj)
    return response.data
}

/* 특정한 번호의 댓글을 조회하고 수정할 수 있는 기능 구성 */
// 댓글 조회 GET 방식 | 댓글 수정 PUT 방식
async function getReply(rno) {
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj) {
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

/* 댓글 삭제 */
async function removeReply(rno) {
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}
function get_clusters(url)
{
    return axios({
        method: 'get',
        url: url+'/clusters/list-clusters',
        responseType: 'json'
        }).then(function (response) {
            return response.data;
            /*
            response.data.forEach(element => {
                console.log(element.name +' '+ element.clusterId);
                return this.clusters.push({name:element.name, id:element.clusterId})
            })*/
        });
}

async function parseCluster(url) {
    let data = await get_clusters(url)
    if (data) {
        data.forEach(element => {
            console.log(element.name +' '+ element.clusterId);
            return this.clusters.push({name:element.name, id:element.clusterId})
        });
    }
}
const checkToken = (r) => {
    
    const access_token = r.variables.header_token;
    // we know that a jwt is composed of three parts:
    //     HEADER.PAYLOAD.SIGNATURE
    // since the contents of a jwt are NOT encrypted, we can just take the payload, base64 decode it and read
    // what's inside!


    const split = access_token.split(".");
    if(split.length < 3){
        r.return(401);
        return;
    }

    const decoded_payload = Buffer.from(split[1], 'base64').toString();    
    try{
        const json_payload = JSON.parse(decoded_payload);
        if(json_payload["client_id"] === "aggregator"){
            r.return(204);
            return;
        }
        if(!json_payload["email_verified"]){
            r.return(403);
            return;
        }
        r.return(204);
    }catch(err){
        r.return(401);
    }
    

} 

export default {
    checkToken
}
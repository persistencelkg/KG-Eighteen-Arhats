-- Lua 脚本
local key = KEYS[1] -- JSON 数据存储的 Redis 键
local json_data = redis.call("GET", key)
if not json_data then
    return redis.error_reply("Key does not exist")
end

local cjson = require "cjson"
local data = cjson.decode(json_data)

-- 使用 ARGV 对 JSON 数据进行更新
for i = 1, #ARGV, 2 do
    local field = ARGV[i]
    local value = ARGV[i + 1]
    data[field] = value
end

local new_json_data = cjson.encode(data)
local res = redis.call("SET", key, new_json_data)
if res.ok == "OK" then
    local ttl =  redis.call('TTL', key)
    redis.call('EXPIRE', key , ttl)
end
return new_json_data
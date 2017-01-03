from aiohttp import web

app = web.Application()


LED_IS_ON = False


async def toggle(request):
    global LED_IS_ON
    LED_IS_ON = not LED_IS_ON
    state = 'on' if LED_IS_ON else 'off'
    return web.json_response({'message': 'LED is {}'.format(state)})


async def status(request):
    global LED_IS_ON
    led_status = 'on' if LED_IS_ON else 'off'
    return web.json_response({'status': led_status})


async def set_brightness(request):
    global LED_IS_ON
    if LED_IS_ON:
        return web.json_response({'message': 'Can not update disabled LED'},
                                 status=400)
    level = request.rel_url.query.get('level', '0')
    if not level.isdigit():
        return web.json_response({'message': 'Not a number'},
                                 status=400)
    level = int(level)
    if not 0 <= level <= 255:
        return web.json_response({'message': 'Incorrect value, correct values is between 0 and 255'},
                                 status=400)
    return web.json_response({'status': 'brightness changed'})

app.router.add_get('/toggle', toggle)
app.router.add_get('/status', status)
app.router.add_get('/set', set_brightness)

if __name__ == '__main__':
    web.run_app(app)

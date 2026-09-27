# HeRoBot for Minecraft 26.2

A port of [HeRoBot](https://github.com/HerobaneNair/HeRoBot) to Minecraft 26.2 (Fabric).

## About

This is a fork of HeRoBot adapted to work on Minecraft 26.2. The original mod was built for an earlier version and several mixins broke with the 26.2 rendering pipeline refactor.

## What was fixed

- **MC 26.2 compatibility** - removed broken `@Redirect` mixins that crashed on launch
- **Knockback physics** - vanilla-accurate knockback with proper velocity handling:
  - Velocity halving (`base / 2.0 + knockback`) matching vanilla `LivingEntity.knockback()`
  - Sprint knockback accumulation (attack + sprint bonus combine correctly)
  - Smooth multi-tick flight via pending velocity + per-tick position sync
  - Correct direction (knockback pushes bot away from attacker)
- **Position sync** - bot movement is broadcast to all clients via `ClientboundTeleportEntityPacket`
- **Tick ordering fix** - knockback velocity is applied before `aiStep()` processes it

## Credits

- **Original mod**: [HerobaneNair/HeRoBot](https://github.com/HerobaneNair/HeRoBot) by HerobaneNair
- **26.2 port**: [DoctoCapybara](https://github.com/DoctoCapybara)

## License

This project is licensed under [CC BY-NC-SA 4.0](LICENSE.txt), same as the original.

Based on HeRoBot by HerobaneNair. Any derivative works must be shared under the same license and used non-commercially.

package kz.narxoz.finaljrpg.battle;

public interface ProjectileHitEffect {
    void onHit(BattleSession session, Projectile projectile, BattleUnit target);
}

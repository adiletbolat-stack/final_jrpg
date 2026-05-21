package kz.narxoz.finaljrpg.map.collision;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.World;

public abstract class AbstractCollisionHandler implements CollisionHandler {

    private CollisionHandler next;

    @Override
    public CollisionHandler setNext(CollisionHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public void handle(MapObject object, World world) {
        if (canHandle(object)) {
            createCollision(object, world);
            return;
        }

        if (next != null) {
            next.handle(object, world);
        }
    }

    protected abstract boolean canHandle(MapObject object);

    protected abstract void createCollision(MapObject object, World world);
}

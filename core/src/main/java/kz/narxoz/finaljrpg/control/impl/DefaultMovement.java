package kz.narxoz.finaljrpg.control.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import kz.narxoz.finaljrpg.control.Movement;
import kz.narxoz.finaljrpg.unit.Unit;


public class DefaultMovement implements Movement {

    @Override
    public void move(Unit unit, float delta) {
        Body body = unit.getBody();

        if(Gdx.input.isKeyJustPressed(Input.Keys.W)) body.applyLinearImpulse(0, 2f, body.getWorldCenter().x, body.getWorldCenter().y, true);
        if(Gdx.input.isKeyPressed(Input.Keys.D)) body.setLinearVelocity(4f, body.getLinearVelocity().y);
        else if(Gdx.input.isKeyPressed(Input.Keys.A)) body.setLinearVelocity(-4f, body.getLinearVelocity().y);
        else body.setLinearVelocity(0, body.getLinearVelocity().y);
    }
}

import java.awt.*;
import java.util.*;

public class Robot {
    double x, y;
    double vx, vy;
    double dt;
    double m;
    double fMax;
    ArrayList obstacles;
    public double diam;
    Obstacle target;
    boolean virtualforce = false;

    public Robot(Point p, ArrayList obstacles, double dt, double m, double fMax, double diam) {
        this.diam = diam;
        this.fMax = fMax;
        this.m = m;
        this.dt = dt;
        vx = vy = 0;
        this.x = p.x;
        this.y = p.y;
        this.obstacles = obstacles;
        this.target = (Obstacle) obstacles.get(0);
    }

    public void updatePosition() {
        double dirX = 0, dirY = 0;
        double minS = 200;
        Iterator iter = obstacles.iterator();
        while (iter.hasNext()) {
            Obstacle ob = (Obstacle) iter.next();
            double distSq = ob.distanceSq(this);
            if (distSq < 1)
                Math.sin(1);
            double dx = ob.charge * (ob.p.x - x) / distSq;
            double dy = ob.charge * (ob.p.y - y) / distSq;
            dirX += dx;
            dirY += dy;
        }
        double norm = Math.sqrt(dirX * dirX + dirY * dirY);
        dirX = dirX / norm;
        dirY = dirY / norm;
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Obstacle ob = (Obstacle) iter.next();
            if (!range(ob, 1200)) continue;
            double distSq = ob.distanceSq(this);
            double dx = (ob.p.x - x);
            double dy = (ob.p.y - y);
//add normal noise to simulate the sonar effect
            dx = addNoise(dx, 0, 1);
            dy = addNoise(dy, 0, 1);
            double safety = distSq / ((dx * dirX + dy * dirY));
            if ((safety > 0) && (safety < minS))
                minS = safety;
        }
        if (minS < 5) {
            double oc = target.charge;
            target.charge *= minS / 5;
            System.out.println(oc + " DOWN TO " + target.charge);
        }
        if (minS > 100) {
            double oc = target.charge;
            target.charge *= minS / 100;
            System.out.println(oc + " UP TO " + target.charge);
        }
        double vtNorm = minS / 2;
        double vtx = vtNorm * dirX;
        double vty = vtNorm * dirY;
        double fx = m * (vtx - vx) / dt;
        double fy = m * (vty - vy) / dt;
        double fNorm = Math.sqrt(fx * fx + fy * fy);
        if (fNorm > fMax) {
            fx *= fMax / fNorm;
            fy *= fMax / fNorm;
        }
        vx += (fx * dt) / m;
        vy += (fy * dt) / m;
//virtual force component
        if (virtualforce && (target.charge < 1000) && (x > 25) && (y > 25)) {
            System.out.println("Virtual Force");
            target.charge *= minS / 100;
            vx = vx + 5;
        }
        x += vx * dt;
        y += vy * dt;
    }

    boolean range(Obstacle ob, double range) {
        double dist = ob.distanceSq(this);
        if (dist < range)
            return true;
        else
            return false;
    }

    double addNoise(double x, double mean, double stddev) {
        Random r = new Random();
        double noise = stddev * r.nextGaussian() + mean;
        return x + noise;
    }
}

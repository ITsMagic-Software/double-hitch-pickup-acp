/** @Author ITsMagic */
private int maxSkids = 12;
private float skidLength = 0.6f;
private float skidWidth = 0.3f;
private float skidSoundSpeed = 0.5f;

public float groundOffset = 0.05f;
public float skidTolerance = 0.8f;

public float skidSoundVolume = 0.5f;
public float skidSoundFadeOut = 0.25f;
public MaterialFile materialFile;

private Vertex vertex = new Vertex();

private ModelRenderer mr;
@AutoWired
private VehicleWheel vw;
private int buildedMaxSkids = -1;
private Vector3Buffer vertices;
private Vector3Buffer normals;
private Vector2Buffer uvs;
private Point3Buffer triangles;
private Skid[] skids;
private int skidIndex;

private final int verticesPerSkid = 4;
private final int trianglesPerSkid = 2;

private float externalEmit = 0;

@AutoWired
private SoundPlayer sp;

private float timeNoSound;

/// Run only once
@Override
public void start() {
  mr = new ModelRenderer();
  SpatialObject o = new SpatialObject(myObject.name + "skidmarks");
  o.addComponent(mr);
  mr.setMaterialFile(materialFile);
  mr.setCastShadowEnabled(false);
  mr.vertex = vertex;
} 

/// Repeat every frame
@Override
public void repeat() {
  buildVertex();

  {
    timeNoSound += Math.bySecond();
    float volume = (skidSoundFadeOut - timeNoSound) / skidSoundFadeOut;
    volume *= 0.3f;
    sp.volume = Math.clamp(volume * skidSoundVolume, 0.0f, 0.8f);
    if (timeNoSound >= skidSoundFadeOut && sp.loop) {
      sp.loop = false;
      sp.speed = skidSoundSpeed;
      sp.stop();
    }
  }

  float s = vw.skidding;
  s = Math.clamp(0f, s, 1f);
  s += externalEmit;
  if (s > skidTolerance) {
    internalEmit(s);
  }
}

public void emit(float s) {
  externalEmit = s;
}

private void internalEmit(float s) {
  if (vw.isGrounded()) {
    s = Math.clamp(0, s, 1);
    Vector3 p = calculateCollisionPoint();
    Vector3 d = calculateMovimentDirection();
    Vector3 l = calculateCrossOfForward(d);
    Skid latest = getLatestSkid();
    float dis = latest.pos.sqrDistance(p);
    float md = (skidLength * 2);
    md *= md;
    if (dis >= md) {
      Skid cs = getCurrentSkid();
      cs.pos.set(p);
      cs.dir.set(d);
      cs.leftDir.set(l);
      nextSkid();
      updateVertex();

      sp.play();
      sp.loop = true;
      sp.speed = skidSoundSpeed;
      timeNoSound = 0;
    }
  }
}

void updateVertex() {
  float md = (skidLength * 2);
  md *= md;
  Vector3 pv = new Vector3();
  Skid latestSkid = null;
  for (int x = 0; x < maxSkids; x++) {
    Skid skid = skids[x];
    int startVerticeIndex = verticesPerSkid * x;
    int startTriangle = trianglesPerSkid * x;

    if (x == 0) {
      latestSkid = skids[skids.length - 1];
    }

    if (latestSkid != null) {
      float dist = latestSkid.pos.sqrDistance(skid.pos);
      if (dist > (md) + (0.5f)) {
        latestSkid = null;
      }
    }

    float w = skidWidth / 2f;
    float l = skidLength / 2f;

    Vector3 p = skid.pos;
    Vector3 forward = skid.dir;
    Vector3 left = skid.leftDir;
    Vector3 normal = forward.cross(left);

    normals.set(startVerticeIndex + 0, normal);
    normals.set(startVerticeIndex + 1, normal);
    normals.set(startVerticeIndex + 2, normal);
    normals.set(startVerticeIndex + 3, normal);

    {
      pv.set(p);
      pv.sumLocal(forward * -l);
      pv.sumLocal(left * w);

      if (latestSkid != null) {
        pv.set(latestSkid.v1);
      }

      vertices.set(startVerticeIndex + 0, pv);
      skid.v0.set(pv);
    }

    {
      pv.set(p);
      pv.sumLocal(forward * l);
      pv.sumLocal(left * w);

      vertices.set(startVerticeIndex + 1, pv);
      skid.v1.set(pv);
    }

    {
      pv.set(p);
      pv.sumLocal(forward * l);
      pv.sumLocal(left * -w);
      vertices.set(startVerticeIndex + 2, pv);
      skid.v2.set(pv);
    }

    {
      pv.set(p);
      pv.sumLocal(forward * -l);
      pv.sumLocal(left * -w);

      if (latestSkid != null) {
        pv.set(latestSkid.v2);
      }

      vertices.set(startVerticeIndex + 3, pv);
      skid.v3.set(pv);
    }
    latestSkid = skid;
  }
  vertex.apply();
}

void buildVertex() {
  boolean rebuild = false;
  if (buildedMaxSkids != maxSkids) {
    rebuild = true;
  }
  if (skids == null) {
    rebuild = true;
  }

  if (rebuild) {
    buildedMaxSkids = maxSkids;
    skids = new Skid[maxSkids];
    for (int x = 0; x < maxSkids; x++) {
      skids[x] = new Skid();
    }

    int verticesCount = maxSkids * verticesPerSkid;
    int trianglesCount = maxSkids * trianglesPerSkid;
    vertices = new Vector3Buffer(verticesCount);
    normals = new Vector3Buffer(verticesCount);
    uvs = new Vector2Buffer(verticesCount);
    triangles = BufferUtils.createPoint3Buffer(trianglesCount);

    for (int x = 0; x < maxSkids; x++) {
      int startVerticeIndex = verticesPerSkid * x;
      int startTriangle = trianglesPerSkid * x;
      triangles.set(startTriangle + 0, startVerticeIndex + 2, startVerticeIndex + 1, startVerticeIndex + 0);
      triangles.set(startTriangle + 1, startVerticeIndex + 3, startVerticeIndex + 2, startVerticeIndex + 0);
    }

    for (int x = 0; x < verticesCount; x++) {
      vertices.set(x, 0, 0, 0);
    }

    for (int x = 0; x < maxSkids; x++) {
      Skid skid = skids[x];
      int startVerticeIndex = verticesPerSkid * x;

      uvs.set(startVerticeIndex + 0, 0, 0);
      uvs.set(startVerticeIndex + 1, 0, 1);
      uvs.set(startVerticeIndex + 2, 1, 1);
      uvs.set(startVerticeIndex + 3, 1, 0);
    }

    vertex.setVertices(vertices);
    vertex.setTriangles(triangles);
    vertex.setNormals(normals);
    vertex.setUVs(uvs);
    vertex.apply();
  }
}

Skid getLatestSkid() {
  if (skidIndex == 0) return skids[skids.length - 1];
  int latest = skidIndex - 1;
  return skids[latest];
}

Skid getCurrentSkid() {
  return skids[skidIndex];
}

void nextSkid() {
  if (skidIndex >= skids.length - 1) {
    skidIndex = 0;
    return;
  }
  skidIndex++;
}

Vector3 calculateMovimentDirection() {
  VehiclePhysics vp = myObject.findComponentInParent(VehiclePhysics.class);
  Vector3 d = vp.velocity.normalize();
  return d;
}

Vector3 calculateCrossOfForward(Vector3 moveDir) {
  Vector3 up = myObject.mainParent.up();
  Vector3 l = up.cross(moveDir);
  l = l.normalize();
  return l;
}

Vector3 calculateCollisionPoint() {
  Vector3 p = vw.modelPosition;
  Vector3 d = myObject.mainParent.down();
  p.sumLocal(d * (vw.radius - groundOffset));
  return p;
}

private static class Skid {
  Vector3 pos = new Vector3();
  Vector3 dir = new Vector3();
  Vector3 leftDir = new Vector3();

  Vector3 v0 = new Vector3();
  Vector3 v1 = new Vector3();
  Vector3 v2 = new Vector3();
  Vector3 v3 = new Vector3();
}
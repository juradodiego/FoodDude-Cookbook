export default function NavBar() {
  return (
    <div className="bg-white h-20 shadow-md sticky top-0 flex items-center px-5">
      {/* Website Name */}
      <div className="flex text-3xl select-none">
        <h1>Food</h1>
        <h1 className="text-green-500">Dude</h1>
      </div>

      {/* Nav items */}
      <div className="flex space-x-5 ml-auto mx-auto my-auto">
        <span>Home</span>
        <span>Recipes</span>
      </div>

      {/* User Profile */}
      <div className="rounded-full h-10 w-10 bg-black mr-5 ml-28"></div>
    </div>
  );
}
